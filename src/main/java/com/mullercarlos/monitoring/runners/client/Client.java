package com.mullercarlos.monitoring.runners.client;

import com.mullercarlos.monitoring.cli.CliArgs;
import com.mullercarlos.monitoring.message.*;
import com.mullercarlos.monitoring.models.Service;
import com.mullercarlos.monitoring.runners.RunnerInterface;
import lombok.*;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

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
        //primeira mensagem para cadastar o cliente no serivdor
        try {
            Socket socket = new Socket(split[0], Integer.parseInt(split[1]));
            @Cleanup MessageHandler messageHandler = new MessageHandler(socket,args.isVerbose());
            /**
             * TODO a way to add services to monitor
             */
            Signin SIGNIN = new Signin("authKey", List.of(Service.builder().name("service").cpuUsage("1").ramUsage("1").build()), 8080);
            messageHandler.sendMessage(SIGNIN);
            //TODO TRATAR RESPOSTA
            Message response = messageHandler.receiveMessage();
        } catch (IOException e) {
            if (e instanceof ConnectException) {
                System.out.println("Como não me cadastrei no servidor vou parar, não consegui conectar no servidor");
            } else {
                e.printStackTrace();
                System.out.println("contacte o mantenedor do software");
            }
            return;
        }

        //Thread que atualiza o servidor com as informações a cada minuto
        new Thread(() -> {
            int failed = 0;
            while (true) {
                try {
                    //https://github.com/oshi/oshi/blob/master/oshi-core/src/test/java/oshi/SystemInfoTest.java preciso fazer isso e montar um HEALTH
                    //TODO maybe put this on constructor of health messages?
                    SystemInfo si = new SystemInfo();
                    GlobalMemory memory = si.getHardware().getMemory();
                    double systemCpuLoad = si.getHardware().getProcessor().getSystemCpuLoad();
                    long available = memory.getAvailable();
                    long total = memory.getTotal();
                    Health health = new Health(systemCpuLoad * 100 + "%", ((total - available) + "/" + total), "", "authKey");
                    @Cleanup MessageHandler handler = new MessageHandler(new Socket(split[0], Integer.parseInt(split[1])), true);
                    handler.sendMessage(health);
                    //SHOULD see if failed
                    System.out.println(handler.receiveMessage());
                    Thread.sleep(60000);
                    failed = 0;
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    failed++;
                    if (failed >= 10) {
                        System.out.println("Muitas falhas ao se comunicar com o serivdor o programa cliente vai para!");
                        System.exit(123);
                    }
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e1) {
                    }
                }
            }
        }).start();
        /**
         * Thread que ouve as mensagens de start stop e follow
         */
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
                            new MessageHandler(serverSocket.accept(), args.isVerbose())
                    ).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
