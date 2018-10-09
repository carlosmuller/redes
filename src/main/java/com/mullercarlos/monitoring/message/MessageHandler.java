package com.mullercarlos.monitoring.message;

import com.mullercarlos.monitoring.models.ClientModel;
import com.mullercarlos.monitoring.utils.JSONUtils;
import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class MessageHandler extends Thread {

    protected final Socket socket;
    protected final BufferedReader input;
    protected final PrintWriter output;
    private final boolean verbose;
    private final String UUID;
    private Map clients;

    public MessageHandler(Socket socket, boolean verbose) throws IOException {
        this.verbose = verbose;
        this.socket = socket;
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.UUID = java.util.UUID.randomUUID().toString();
    }

    public MessageHandler(Socket socket, Map client, boolean verbose) throws IOException {
        this(socket, verbose);
        this.clients = client;
    }

    void handle() {
        Message message = receiveMessage();
        /**
         * Quando clients é null significa que quem está recendo mensagem é o cliente os tipos são {@link com.mullercarlos.monitoring.message.Type#START START}, {@link com.mullercarlos.monitoring.message.Type#STOP STOP}
         * e {@link com.mullercarlos.monitoring.message.Type#FOLLOW FOLLOW}
         */
        if(clients != null){//Mensagens do cliente para o Servidor
            if(message instanceof Signin){
                Signin signin = (Signin) message;
                ClientModel clientModel = ClientModel.builder().authKey(signin.getAuthKey()).serviceList(signin.getServiceList()).port(signin.getPortListener())
                        .ip(socket.getInetAddress()
                                .getHostAddress()).build();
                String authKey = clientModel.getAuthKey();
                if(clients.containsKey(authKey)){
                    if (verbose) {
                        System.out.println(UUID + " - Existe um cliente com a chave: ["+ authKey+"]");
                    }
                    ClientModel existent = (ClientModel)clients.get(authKey);
                    if(!existent.equals(clientModel)){//bloqueia caso usem a a mesma chave mas são diferentes
                        if (verbose) {
                            System.out.println(UUID + " - Os clientes eram diferentes então bloqueie existent["+existent+"] da mensagem["+clientModel+"]");
                        }
                        sendMessage(new Failed("not allowed"));
                        return;
                    }
                }
                if (verbose) {
                    System.out.println(UUID + " - Colocando o cliente no mapa de autorizados: ["+clientModel+"]");
                }
                clients.putIfAbsent(authKey, clientModel);
                sendMessage(new Ok("Consegui te cadstrar com sucesso", authKey));
                return;
            }
            if (message instanceof Health){
                Health healthUpdate = ((Health) message);
                if(clients.containsKey(healthUpdate.getAuthKey())){//Para tratar essa mensagem o cliente tem que ter mandado antes a de SINGIN
                    ClientModel signedIn = (ClientModel)clients.get(healthUpdate.getAuthKey());
                    String hostAddress = socket.getInetAddress().getHostAddress();
                    if(!signedIn.getIp().equals(hostAddress)){// uma chave só pode responder para um ip
                        if (verbose) {
                            System.out.println(UUID + " - BLOQUEADO - Os ips diferem para a chave["+healthUpdate.getAuthKey()+"] cadastrado["+signedIn.getIp()+"] da mensagem["+hostAddress+"]");
                        }
                        sendMessage(new Failed("not allowed"));
                        return;
                    }
                    signedIn.updateHealth(healthUpdate);
                    sendMessage(new Ok("Health updated", healthUpdate.getAuthKey()));
                }else{//Bloqueia caso não tenha ainda sido cadastrado
                    sendMessage(new Failed("You should send signin first!"));
                    return;
                }
            }
        }else{//Mensagens do servidor para o cliente

        }
    }

    public void sendMessage(Message message) {
        String messageJson = JSONUtils.serialize(message);
        if(this.verbose) System.out.println(UUID + " - Enviando a mensagem : ["+message+"]");
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
        if(this.verbose) System.out.println(UUID + " - Recebi a mensagem : ["+message+"]");
        return message;
    }

    public void close() throws IOException {
        this.output.close();
        this.input.close();
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
