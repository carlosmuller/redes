package com.mullercarlos.monitoring.runners;

import com.mullercarlos.monitoring.main.CliArgs;
import com.mullercarlos.monitoring.message.*;
import com.mullercarlos.monitoring.models.Service;
import com.mullercarlos.monitoring.runners.*;
import com.mullercarlos.monitoring.utils.ListenerThreadBuilder;
import lombok.*;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

import java.io.IOException;
import java.net.*;
import java.util.*;

import static java.lang.Thread.sleep;

@ToString(callSuper = true)
public class Client extends RunnerInterface {

    private final String authKey;
    private final String server;

    public Client(CliArgs args) {
        super(args);
        this.authKey = args.getAuthKey();
        this.server = args.getServer();
    }

    @Override
    public void run() {
        if (server == null || server.isEmpty() || !server.contains(":")) {
            System.out.println("O server não pode ser nula, e deve seguir o seguinte padrão 127.0.0.0:8080(ip:porta)");
            return;
        }
        if (authKey == null || authKey.isEmpty()) {
            System.out.println("A chave de acesso não pode ser nula");
            return;
        }
        String[] split = server.split(":");
        //primeira mensagem para cadastar o cliente no serivdor
        if (sendSignIn(split)) return;
        //Star uma thread para escutar mensagens do servidor caso falhe para o programa
        Thread listenerThread = new ListenerThreadBuilder(this).getListenerThread("Thread que escuta mensagens vindas do servidor");
        listenerThread.start();
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        if (!listenerThread.isAlive()) {
            System.out.println("não consegui rodar a thread que escuta mensagens do servidor vou parar!");
            return;
        }
        System.out.println("Thread de escuta na porta" + args.getPort());
        //Thread que atualiza o servidor com as informações a cada minuto
        new Thread(() -> {
            int failed = 0;
            while (true) {
                try {
                    sendHealthUpdate(split);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    sleep(60000);
                    Message response = sendHealthUpdate(split);
                    if (response instanceof Failed) {
                        String failedReason = ((Failed) response).getMessage();
                        if (!failedReason.contains("You should send signin first!")) {
                            throw new RuntimeException("Não consegui dar o update de health erro" + failedReason);
                        } else {
                            if (!sendSignIn(split)) {
                                System.exit(43);
                            }
                        }
                    }
                    failed = 0;
                } catch (IOException | InterruptedException | RuntimeException e) {
                    failed++;
                    if (failed >= 10) {
                        System.out.println("Muitas falhas ao se comunicar com o serivdor o programa cliente vai para!");
                        System.exit(42);
                    }
                }
            }
        }).start();
    }

    private Message sendHealthUpdate(String[] split) throws IOException {
        Health health = buildHealthMessage();
        String uuid = UUID.randomUUID().toString();
        @Cleanup MessageHandler handler = new MessageHandler(new Socket(split[0], Integer.parseInt(split[1])), args.isVerbose(), uuid);
        handler.sendMessage(health);
        return handler.receiveMessage();
    }

    private boolean sendSignIn(String[] split) {
        try {
            Socket socket = new Socket(split[0], Integer.parseInt(split[1]));
            String uuid = UUID.randomUUID().toString();
            MessageHandler messageHandler = new MessageHandler(socket, args.isVerbose(), uuid);
            /**
             * TODO a way to add services to monitor
             */
            Signin SIGNIN = new Signin(this.authKey, List.of(Service.builder().name("service").status("running").build()), args.getPort());
            messageHandler.sendMessage(SIGNIN);
            Message response = messageHandler.receiveMessage();
            if (response instanceof Failed) {
                System.out.println("Não consegui me cadastrar " + ((Failed) response).getMessage());
                return true;
            }
            messageHandler.close();
        } catch (IOException e) {
            if (e instanceof ConnectException) {
                System.out.println("Como não me cadastrei no servidor vou parar, não consegui conectar no servidor");
            } else {
                e.printStackTrace();
                System.out.println("contacte o mantenedor do software");
            }
            return true;
        }
        return false;
    }


    //https://github.com/oshi/oshi/blob/master/oshi-core/src/test/java/oshi/SystemInfoTest.java
    private Health buildHealthMessage() {
        SystemInfo si = new SystemInfo();
        GlobalMemory memory = si.getHardware().getMemory();
        double systemCpuLoad = si.getHardware().getProcessor().getSystemCpuLoad();
        long available = memory.getAvailable();
        long total = memory.getTotal();
        return new Health(systemCpuLoad * 100, total - available, total, 0L, this.authKey);
    }


    @Override
    public MessageHandler getMessageHandler(Socket socket, String uuid) throws IOException {
        return new MessageHandler(socket, isVerbose(), uuid, this.authKey);
    }

}
