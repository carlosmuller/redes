package com.mullercarlos.monitoring.runners.server;

import com.mullercarlos.monitoring.runners.RunnerInterface;
import com.mullercarlos.monitoring.runners.client.Client;

import java.io.IOException;
import java.net.*;
import java.util.UUID;

public class ListenerThreadBuilder {
    private final RunnerInterface runner;

    public ListenerThreadBuilder(Server runner) {
        this.runner = runner;
    }

    public ListenerThreadBuilder(Client runner) {
        this.runner = runner;
    }

    public Thread getListenerThread(String threadName) {
        return new Thread(() -> {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(runner.getPort());
                //timeout do accept para que consiga para a thread
                serverSocket.setSoTimeout(10);
                while (true) {
                    try {

                        String uuid = UUID.randomUUID().toString();
                        Socket accept = serverSocket.accept();
                        new Thread(
                                runner.getMessageHandler(accept, uuid)
                                , "Thread de tratamento de mensagem - " + uuid
                        ).start();
                    } catch (IOException e) {
                        if (Thread.interrupted()) {
                            System.out.println("Fechando "+ Thread.currentThread().getName());
                            serverSocket.close();
                            return;
                        }
                    }
                }

            } catch (IOException e) {
                if (e instanceof BindException) {
                    System.out.println("Vou parar endereço já em uso");
                }
            }
        }, threadName);
    }
}