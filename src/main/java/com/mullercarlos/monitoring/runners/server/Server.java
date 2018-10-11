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

import static java.util.stream.Collectors.toList;

@ToString(callSuper = true)
public class Server extends RunnerInterface {

    public Server(CliArgs args) {
        super(args);
    }

    private static final ConcurrentHashMap<String, ClientModel> clientKeys = new ConcurrentHashMap<>();

    @Override
    public void run() {
        Thread thread_de_escuta_de_clientes = new Thread(() -> {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                if(e instanceof BindException){
                    System.out.println("Vou parar endereço já em uso");
                    return;
                }
            }
            while (true) {
                try {

                    String uuid = UUID.randomUUID().toString();
                    new Thread(
                            new MessageHandler(serverSocket.accept(), clientKeys, args.isVerbose(), uuid)
                            , "Thread de tratamento de mensagem - " + uuid
                    ).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread de escuta de clientes");
        thread_de_escuta_de_clientes.start();
        System.out.println("Theread de escuta aberta ouvindo na porta " + args.getPort());
        Scanner scanner = new Scanner(System.in);
        int option = -1;
        do {
            System.out.println("Digite uma das opções:\n 1) Gerar chave para cliente\n 2)Listar clientes e status\n 3)Seguir um arquivo de cliente\n 4)Fechar programa");
            option = scanner.nextInt();
            switch (option) {
                case 1:{
                    System.out.println("Nova chave " + UUID.randomUUID().toString());
                    break;
                }
                case 2:{
                    if(clientKeys.isEmpty()) System.out.println("Sem clientes");
                    clientKeys.forEach((key, client) -> System.out.println("Chave [" + key + "]  status [" + client + "]"));
                    break;
                }
                case 3:{
                    try{
                        followOption(scanner);
                    }catch (Exception e){
                     e.printStackTrace();
                    }
                    break;
                }
                case 4:
                    thread_de_escuta_de_clientes.interrupt();
                    return;
                default:
                    break;
            }
            System.out.println(option);
        } while (option != 4);


    }

    @SneakyThrows
    private void followOption(Scanner scanner) {
        System.out.println("Selecione o cliente:");
        List<ClientModel> clients = clientKeys.values().stream().collect(toList());
        int totalClients = clients.size();
        System.out.println("0) para sair");
        for (int i = 0; i < totalClients; i++) {
            ClientModel client = clients.get(i);
            System.out.println((i + 1) + ") " + client.getIp() + ":"+ client.getPort() +" - chave de acesso " + client.getAuthKey());
        }
        int clientOption=-42;
        try {
            clientOption = scanner.nextInt();
            if (clientOption > totalClients) {
                System.out.println("Numero inválido");
                followOption(scanner);
            }
            if (clientOption-1 == -1) return;
        } catch (Exception e) {
            System.out.println("digite um numero:");
            followOption(scanner);
        }
        System.out.println("Digite o caminho no servidor remoto");
        scanner.nextLine();
        String file = scanner.nextLine();
        if(null == file || file.isEmpty()){
            System.out.println("Digite algo");
            followOption(scanner);
        }
        scanner.reset();
        System.out.println("Pressione q para sair");
        ClientModel client = clients.get(clientOption-1);
        Socket socket = new Socket(client.getIp(), client.getPort());
        MessageHandler handler = new MessageHandler(socket,args.isVerbose(), UUID.randomUUID().toString());
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
}
