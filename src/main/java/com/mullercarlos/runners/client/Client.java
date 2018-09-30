package com.mullercarlos.runners.client;

import com.mullercarlos.cli.CliArgs;
import com.mullercarlos.message.MessageHandler;
import com.mullercarlos.runners.RunnerInterface;
import lombok.*;

import java.io.*;
import java.net.*;

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
            messageHandler.sendMessage("huias huias huias\n\n\n\n\n uhashuiashiudasiudh \n ashudiashdiuashdiuasd", false);
            String message = messageHandler.receiveMessage(false);
            System.out.println("RECEBI A MENSAGEM :  " + message);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
