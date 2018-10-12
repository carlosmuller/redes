package com.mullercarlos.monitoring.message;

import com.mullercarlos.monitoring.models.ClientModel;
import com.mullercarlos.monitoring.utils.JSONUtils;
import lombok.SneakyThrows;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.time.LocalDateTime.now;

public class MessageHandler extends Thread {

    protected final Socket socket;
    protected final BufferedReader input;
    protected final PrintWriter output;
    private final boolean verbose;
    private final String UUID;
    private String authKey;
    private Map clients;

    public MessageHandler(Socket socket, boolean verbose, String UUID) throws IOException {
        this.verbose = verbose;
        this.socket = socket;
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.output.flush();
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.UUID = UUID;
    }

    public MessageHandler(Socket socket, Map client, boolean verbose, String UUID) throws IOException {
        this(socket, verbose, UUID);
        this.clients = client;
    }

    public MessageHandler(Socket accept, boolean verbose, String uuid, String authKey) throws IOException {
        this(accept, verbose, uuid);
        this.authKey = authKey;
    }

    void handle() {
        Message message = receiveMessage();
        /**
         * Quando clients é null significa que quem está recendo mensagem é o cliente
         * os tipos são {@link com.mullercarlos.monitoring.message.Type#START START},
         *  {@link com.mullercarlos.monitoring.message.Type#STOP STOP}
         * e {@link com.mullercarlos.monitoring.message.Type#FOLLOW FOLLOW}
         */
        if (clients != null) {
            //Mensagens do cliente para o Servidor
            if (message instanceof Signin) {
                handleSignin((Signin) message);
                return;
            }
            if (message instanceof Health) {
                handleHealth((Health) message);
                return;
            }
        } else {
            //Mensagens do servidor para o cliente
            if (message instanceof Follow) {
                handleFollow((Follow) message);
                return;
            }
        }
    }

    private void handleFollow(Follow message) {
        Follow follow = message;
        if (follow.getAuthKey().equals(authKey)) {
            String pathOfFile = follow.getPathOfFile();
            Path path = Paths.get(pathOfFile);
            if (!Files.isReadable(path)) {
                if (verbose) {
                    System.out.println(UUID + " - FALHO - Porque o arquivo não é legivel[" + pathOfFile + "]");
                }
                sendMessage(new Failed("Can't read"));
                return;
            }
            sendMessage(new Ok("Can't read", authKey));
            this.output.flush();
            try {
                BufferedReader bufferedReader = Files.newBufferedReader(path);
                while (true) {//atualiza o server a cada 100 milisegundos, caso o server tenha fechado retorna
                    if (socket.isClosed()) {
                        if (verbose) {
                            System.out.println(UUID + " - SUCCESS - conexão fechada do follow[" + pathOfFile + "]");
                        }
                        return;
                    }
                    String s = bufferedReader.readLine();
                    if (s == null) {
                        sleep(100);
                    } else {
                        this.output.println(s);
                        this.output.flush();
                        sleep(100);
                    }
                }
            } catch (IOException e) {
                if(Thread.interrupted()){
                    try {
                        this.close();
                        return;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                e.printStackTrace();
                return;
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void handleHealth(Health message) {
        Health healthUpdate = message;
        String authKey = healthUpdate.getAuthKey();
        if (clients.containsKey(authKey)) {//Para tratar essa mensagem o cliente tem que ter mandado antes a de SINGIN
            ClientModel signedIn = (ClientModel) clients.get(authKey);
            String hostAddress = socket.getInetAddress().getHostAddress();
            if (!signedIn.getIp().equals(hostAddress)) {// uma chave só pode responder para um ip
                if (verbose) {
                    System.out.println(UUID + " - BLOQUEADO - Os ips diferem para a chave[" + authKey + "] cadastrado[" + signedIn.getIp() + "] da mensagem[" + hostAddress + "]");
                }
                sendMessage(new Failed("not allowed"));
                return;
            }
            signedIn.updateHealth(healthUpdate);
            sendMessage(new Ok("Health updated", authKey));
        } else {//Bloqueia caso não tenha ainda sido cadastrado
            if (verbose) {
                System.out.println(UUID + " - BLOQUEADO - Por não achar o a chame no mapa chave[" + authKey + "]");
            }
            sendMessage(new Failed("You should send signin first!"));
            return;
        }
    }

    /**
     * Responvasel por lidar com mesnagems do tipo health, checa se já existe alguem com aquela chave
     * @param message
     * @return
     */
    private boolean handleSignin(Signin message) {
        Signin signin = message;
        ClientModel clientModel = ClientModel.builder().authKey(signin.getAuthKey())
                .serviceList(signin.getServiceList())
                .port(signin.getPortListener())
                .lastHealthCheck(now())
                .ip(socket.getInetAddress()
                        .getHostAddress()).build();
        String authKey = clientModel.getAuthKey();
        if (clients.containsKey(authKey)) {
            if (verbose) {
                System.out.println(UUID + " - Existe um cliente com a chave: [" + authKey + "]");
            }
            ClientModel existent = (ClientModel) clients.get(authKey);
            if (!existent.equals(clientModel)) {//bloqueia caso usem a a mesma chave mas são diferentes
                if (verbose) {
                    System.out.println(UUID + " - BLOQUEADO - Os clientes eram diferentes então bloqueie existent[" + existent + "] da mensagem[" + clientModel + "]");
                }
                sendMessage(new Failed("not allowed"));
                return true;
            }
        }
        if (verbose) {
            System.out.println(UUID + " - Colocando o cliente no mapa de autorizados: [" + clientModel + "]");
        }
        clients.putIfAbsent(authKey, clientModel);
        sendMessage(new Ok("Consegui te cadstrar com sucesso", authKey));
        return false;
    }

    public void sendMessage(Message message) {
        String messageJson = JSONUtils.serialize(message);
        if (this.verbose) System.out.println(UUID + " - Enviando a mensagem : [" + message + "]");
        this.output.println(messageJson);
    }

    @SneakyThrows
    public Message receiveMessage() {
        String line;
        StringBuilder builder = new StringBuilder();
        do {
            line = this.input.readLine();
            builder.append(line);
        } while (this.input.ready());
        String jsonString = builder.toString();
        //Por limitação da lib tive que converter duas vezes uma para a classe Mãe Message e outra para o a classe que está no Type
        Message message = JSONUtils.deserialize(jsonString, Message.class);
        message = (Message) JSONUtils.deserialize(jsonString, message.getType().getClazz());
        if (this.verbose) System.out.println(UUID + " - Recebi a mensagem : [" + message + "]");
        return message;
    }

    //TODO colocar isso no server?
    public void follow(Scanner scanner) {
        Thread systemOutThread = new Thread(() -> {
            do {
                try {
                    String s = this.input.readLine();
                    if (s != null) System.out.println(s);
                    sleep(100);
                } catch (InterruptedException e) {
                    return;
                } catch (IOException e) {
                    if(e instanceof SocketTimeoutException){
                        return;
                    }
                    e.printStackTrace();
                }
            } while (true);
        }, "Impressao do follow");
        systemOutThread.start();
        String option;
        do {
            System.out.println("Pressione q para sair");
            option = scanner.nextLine();
        } while (!option.equals("q"));
        systemOutThread.interrupt();
    }

    public void close() throws IOException {
        this.socket.close();
    }

    @Override
    public void run() {
        try {
            handle();
            this.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
