package com.mullercarlos.monitoring.runners.client;

import com.mullercarlos.monitoring.cli.CliArgs;
import com.mullercarlos.monitoring.message.*;
import com.mullercarlos.monitoring.models.Service;
import com.mullercarlos.monitoring.runners.RunnerInterface;
import lombok.*;

import java.io.IOException;
import java.net.*;
import java.util.List;

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
            Signin SIGNIN = new Signin("authKey", List.of(Service.builder().name("service").cpuUsage("1").ramUsage("1").build()), 8080);
            messageHandler.sendMessage(SIGNIN);
            System.out.println(messageHandler.receiveMessage());

            //Thread que atualiza o servidor com as informações a cada minuto
            new Thread(() -> {
                while (true){
                    try {
                        //https://github.com/oshi/oshi/blob/master/oshi-core/src/test/java/oshi/SystemInfoTest.java preciso fazer isso e montar um HEALTH
                        @Cleanup MessageHandler handler = new MessageHandler(new Socket(split[0], Integer.parseInt(split[1])));
                        handler.sendMessage(SIGNIN);
                        System.out.println(handler.receiveMessage());
                        Thread.sleep(6000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
