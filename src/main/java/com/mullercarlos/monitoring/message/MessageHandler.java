package com.mullercarlos.monitoring.message;

import com.mullercarlos.monitoring.models.ClientModel;
import com.mullercarlos.monitoring.utils.JSONUtils;
import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class MessageHandler extends Thread {

    protected final Socket socket;
    protected final BufferedReader input;
    protected final PrintWriter output;
    private Map clients;

    public MessageHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public MessageHandler(Socket socket, Map client) throws IOException {
        this(socket);
        this.clients = client;
    }

    void handle() {
        Message message = receiveMessage();
        if(message instanceof Signin){
            Signin signin = (Signin) message;
            ClientModel clientModel = ClientModel.builder().authKey(signin.getAuthKey()).serviceList(signin.getServiceList()).port(signin.getPortListener())
                    .ip(socket.getInetAddress()
                            .getHostAddress()).build();
            if(clients.containsKey(clientModel.getAuthKey())){
                ClientModel existent = (ClientModel)clients.get(clientModel.getAuthKey());
                if(!existent.equals(clientModel)){
                    sendMessage(new Failed("not allowed"));
                    return;
                }
            }
            clients.putIfAbsent(clientModel.getAuthKey(), clientModel);
            sendMessage(new Ok("Consegui te cadstrar com sucesso", clientModel.getAuthKey()));
            return;
        }
        if (message instanceof Health){
            Health healthUpdate = ((Health) message);
            if(clients.containsKey(healthUpdate.getAuthKey())){
                ClientModel signedIn = (ClientModel)clients.get(healthUpdate.getAuthKey());
                if(!signedIn.getIp().equals(socket.getInetAddress().getHostAddress())){
                    sendMessage(new Failed("not allowed"));
                    return;
                }
                signedIn.updateHealth(healthUpdate);
                sendMessage(new Ok("Health updated", healthUpdate.getAuthKey()));
            }else{
                sendMessage(new Failed("You should send signin first!"));
            }
        }
    }

    public void sendMessage(Message message) {
//        System.out.println("MANDANDO MESAGEM");
        this.output.println(JSONUtils.serialize(message));
//        System.out.println("MANDEI MESAGEM");
    }

    @SneakyThrows
    public Message receiveMessage() {
//        System.out.println("RECEBENDO MENSAGEM");
        String line;
        StringBuilder builder = new StringBuilder();
        do {
            line = this.input.readLine();
            builder.append(line);
        } while (this.input.ready());
//        System.out.println("RECEBI MENSAGEM");
        String jsonString = builder.toString();
        Message message = JSONUtils.deserialize(jsonString, Message.class);
        message = (Message) JSONUtils.deserialize(jsonString, message.getType().getClazz());
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
