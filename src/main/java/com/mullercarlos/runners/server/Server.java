package com.mullercarlos.runners.server;

import com.mullercarlos.cli.CliArgs;
import lombok.*;
import com.mullercarlos.network.ListenerSocket;
import com.mullercarlos.runners.RunnerInterface;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.concurrent.ConcurrentHashMap;

@ToString(callSuper = true)
public class Server extends RunnerInterface {

    public Server(CliArgs args) {
        super(args);
    }
    private static final ConcurrentHashMap<Inet4Address, String> clientKeys = new ConcurrentHashMap<>();

    @Override
    public void run() {
        ListenerSocket socket = ListenerSocket.builder().port(port).build();
        try {
            socket.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
