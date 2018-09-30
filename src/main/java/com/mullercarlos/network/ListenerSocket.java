package com.mullercarlos.network;

import lombok.*;
import com.mullercarlos.message.MessageHandler;

import java.io.IOException;
import java.net.*;

@Builder
public class ListenerSocket {
    private int port;

    public void run() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            new Thread(
                    new MessageHandler(serverSocket.accept())
            ).start();
        }
    }
}
