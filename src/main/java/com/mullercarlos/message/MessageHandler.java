package com.mullercarlos.message;

import com.mullercarlos.utils.JSONUtils;
import lombok.*;

import java.io.*;
import java.net.Socket;

public class MessageHandler extends Thread {

    private final Socket socket;
    protected final BufferedReader input;
    protected final PrintWriter output;

    public MessageHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void handle() {
        Message message = receiveMessage();
        sendMessage(message);
    }

    public void sendMessage(Message message) {
        System.out.println("MANDANDO MESAGEM");
        this.output.println(JSONUtils.serialize(message));
        System.out.println("MANDEI MESAGEM");
    }

    @SneakyThrows
    public Message receiveMessage() {
        System.out.println("RECEBENDO MENSAGEM");
        String line;
        StringBuilder builder = new StringBuilder();
        do {
            line = this.input.readLine();
            builder.append(line);
        } while (this.input.ready());
        System.out.println("RECEBI MENSAGEM");
        String jsonString = builder.toString();
        Message deserialize = JSONUtils.deserialize(jsonString, Message.class);
        return (Message) JSONUtils.deserialize(jsonString, deserialize.getType().getClazz());
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
