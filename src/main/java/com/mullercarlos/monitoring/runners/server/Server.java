package com.mullercarlos.monitoring.runners.server;

import com.mullercarlos.monitoring.cli.CliArgs;
import com.mullercarlos.monitoring.message.MessageHandler;
import com.mullercarlos.monitoring.models.ClientModel;
import com.mullercarlos.monitoring.runners.RunnerInterface;
import lombok.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

@ToString(callSuper = true)
public class Server extends RunnerInterface {

    public Server(CliArgs args) {
        super(args);
    }

    private static final ConcurrentHashMap<String, ClientModel> clientKeys = new ConcurrentHashMap<>();

    @Override
    public void run() {
        new Thread(() -> {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    new Thread(
                            new MessageHandler(serverSocket.accept(), clientKeys)
                    ).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(() -> {
            while (true) {
                System.out.println("Clientes:");
                clientKeys.forEach((s, clientModel) -> {
                    System.out.println("######clientModel#####\nAuthkey[" + s + "]\nclientModel\n" + clientModel.toString() + "\n#######endofclient####");
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
