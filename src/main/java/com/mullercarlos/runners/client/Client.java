package com.mullercarlos.runners.client;

import com.mullercarlos.cli.CliArgs;
import com.mullercarlos.message.*;
import com.mullercarlos.models.Service;
import com.mullercarlos.runners.RunnerInterface;
import lombok.*;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.stream.IntStream;

@ToString(callSuper = true)
public class Client extends RunnerInterface {

    public Client(CliArgs args) {
        super(args);
    }

    @Override
    public void run() {
        String server = args.getServer();
        String[] split = server.split(":");
        System.out.println(split[0] + ":" + split[1]);
        try {
            Socket socket = new Socket(split[0], Integer.parseInt(split[1]));
            @Cleanup MessageHandler messageHandler = new MessageHandler(socket);
            /**
             * todo preciso colocar um server para ouvir as chamadas do servidor
             */
            Signin SIGNIN = new Signin("authKey", List.of(Service.builder().name("service").cpuUsage("1").ramUsage("1").build()));
            messageHandler.sendMessage(SIGNIN);
            Message message = messageHandler.receiveMessage();
            System.out.println("RECEBI A MENSAGEM :  " + message);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
