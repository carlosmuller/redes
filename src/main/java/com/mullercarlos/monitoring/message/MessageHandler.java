package com.mullercarlos.monitoring.message;

import com.mullercarlos.monitoring.models.Client;
import com.mullercarlos.monitoring.utils.JSONUtils;
import lombok.*;

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

    public void handle() {
        Message message = receiveMessage();
        if(message instanceof Signin){
            Signin signin = (Signin) message;
            Client client = Client.builder().authKey(signin.getAuthKey()).serviceList(signin.getServiceList()).port(signin.getPortListener())
                    .ip(socket.getInetAddress()
                            .getHostAddress()).build();
            if(clients.containsKey(client.getAuthKey())){
                Client existent = (Client)clients.get(client.getAuthKey());
                if(!existent.equals(client)){
                    sendMessage(new Failed("not allowed"));
                    return;
                }
            }
            clients.putIfAbsent(client.getAuthKey(), client);
            sendMessage(new Ok("Consegui te cadstrar com sucesso", client.getAuthKey()));
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
