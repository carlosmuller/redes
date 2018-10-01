package com.mullercarlos.message;

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
        String message = receiveMessage().toUpperCase();
        sendMessage(message);
    }

    public void sendMessage(String message) {
        System.out.println("MANDANDO MESAGEM");
        this.output.println(message + "\n");
        System.out.println("MANDEI MESAGEM");
    }

    @SneakyThrows
    public String receiveMessage() {
        System.out.println("RECEBENDO MENSAGEM");
        String line;
        StringBuilder builder = new StringBuilder();
        do {
            line = this.input.readLine();
            builder.append(line).append('\n');
        } while (this.input.ready());
        System.out.println("RECEBI MENSAGEM");
        return builder.toString();
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
