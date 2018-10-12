package com.mullercarlos.monitoring.runners.server;

import com.mullercarlos.monitoring.cli.CliArgs;
import com.mullercarlos.monitoring.message.*;
import com.mullercarlos.monitoring.models.ClientModel;
import com.mullercarlos.monitoring.runners.RunnerInterface;
import lombok.*;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;
import static java.util.stream.Collectors.toList;

@ToString(callSuper = true)
public class Server extends RunnerInterface {

    public Server(CliArgs args) {
        super(args);
    }

    private static final ConcurrentHashMap<String, ClientModel> clientKeys = new ConcurrentHashMap<>();

    @Override
    public void run() {
        Thread listenerThread = new ListenerThreadBuilder(this).getListenerThread("Thread de escuta mensagens dos clientes");
        listenerThread.start();
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!listenerThread.isAlive()) return;
        System.out.println("Theread de escuta aberta ouvindo na porta " + args.getPort());
        //Thread para checar se o cliente está saudavel a cada min
        Thread threadToCheckHealthOfClients = new Thread(() -> {
            while (true) {
                if (!this.clientKeys.isEmpty()) {
                    this.clientKeys.forEach((key, client) -> client.isHealth());
                }
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    if (Thread.interrupted()){
                        System.out.println("Fechando "+ Thread.currentThread().getName());
                        return;
                    }
                }
            }

        }, "Thread de checar se clientes estão bem");
        threadToCheckHealthOfClients.start();
        //TODO talvez deixar isso mais claro?
        Scanner scanner = new Scanner(System.in);
        int option = -1;
        do {
            System.out.println("Digite uma das opções:\n 1) Gerar chave para cliente\n 2) Listar clientes e status\n 3) Seguir um arquivo de cliente\n 4) Remover cliente\n 5) Fechar programa");
            option = scanner.nextInt();
            switch (option) {
                case 1: {
                    System.out.println("Nova chave " + UUID.randomUUID().toString());
                    break;
                }
                case 2: {
                    if (clientKeys.isEmpty()) System.out.println("Sem clientes");
                    clientKeys.forEach((key, client) -> {
                        System.out.println("Chave [" + key + "]  status [" + client + "] isHealth[" + client.isHealth() + "]");
                    });
                    break;
                }
                case 3: {
                    try {
                        followOption(scanner);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 4: {
                    if (clientKeys.isEmpty()) System.out.println("Sem clientes");
                    ClientModel client = getClient(scanner);
                    if (client!=null) this.clientKeys.remove(client.getAuthKey());
                    break;
                }
                case 5:
                        threadToCheckHealthOfClients.interrupt();
                        listenerThread.interrupt();
                     return;
                default:
                    break;
            }
            System.out.println(option);
        } while (option != 5);


    }

    @Override
    public MessageHandler getMessageHandler(Socket socket, String uuid) throws IOException {
        return new MessageHandler(socket, this.clientKeys, isVerbose(), uuid);
    }

    @SneakyThrows
    private void followOption(Scanner scanner) {
        ClientModel client = getClient(scanner);
        if(client==null)return;
        System.out.println("Digite o caminho no servidor remoto");
        scanner.nextLine();
        String file = scanner.nextLine();
        if (null == file || file.isEmpty()) {
            System.out.println("Digite algo");
            followOption(scanner);
        }
        scanner.reset();
        System.out.println("Pressione q para sair");

        Socket socket = new Socket(client.getIp(), client.getPort());
        MessageHandler handler = new MessageHandler(socket, args.isVerbose(), UUID.randomUUID().toString());
        Follow follow = new Follow(file, client.getAuthKey());
        handler.sendMessage(follow);
        Message message = handler.receiveMessage();
        if (message.getType() == Type.FAILED) {
            System.out.println(((Failed) message).getMessage());
            return;
        }
        handler.follow(scanner);
        handler.close();
    }

    private ClientModel getClient(Scanner scanner) {
        System.out.println("Selecione o cliente:");
        List<ClientModel> clients = clientKeys.values().stream().collect(toList());
        int totalClients = clients.size();
        System.out.println("0) para sair");
        for (int i = 0; i < totalClients; i++) {
            ClientModel client = clients.get(i);
            System.out.println((i + 1) + ") " + client.getIp() + ":" + client.getPort() + " - chave de acesso " + client.getAuthKey());
        }
        int clientOption = -42;
        try {
            clientOption = scanner.nextInt();
            if (clientOption > totalClients) {
                System.out.println("Numero inválido");
                return getClient(scanner);
            }
            if (clientOption - 1 == -1) return null;
        } catch (Exception e) {
            System.out.println("digite um numero:");
            return getClient(scanner);
        }
        return clients.get(clientOption - 1);
    }


}
