package com.mullercarlos.message;

import com.mullercarlos.utils.JSONUtils;
import lombok.*;

import java.io.*;
import java.net.Socket;

public class MessageHandler extends Thread {

    private final Socket socket;
    private final BufferedReader input;
    private final PrintWriter output;

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
        StringBuilder json = new StringBuilder();
        do {
            line = this.input.readLine();
            json.append(line);
        } while (this.input.ready());
        System.out.println("RECEBI MENSAGEM");
        return JSONUtils.deserialize(json.toString(), Message.class);
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
