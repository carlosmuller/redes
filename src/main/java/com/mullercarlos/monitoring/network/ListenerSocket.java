package com.mullercarlos.monitoring.network;

import com.mullercarlos.monitoring.message.MessageHandler;
import lombok.*;

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
