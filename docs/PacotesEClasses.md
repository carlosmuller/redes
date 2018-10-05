A base para os pacotes é `com.mullercarlos.monitoring` começam na pasta `src/main/java`
Os teste das classes estão em `src/test/java`.

# O Pacote **_cli_**

Neles estão as classes ligadas à linha de comando.

 - ## Classe CLI
    É a classe responsável por processar os argumentos passados pela linha de comando ao rodar o programa. 
    Para facilitar e não ter me preocupar tanto com essa parte acabei escolhendo a lib [PicoCli](https://github.com/remkop/picocli).


# O pacote **_main_**

O pacote main é a porta de entrada para execução do programa, a classe Main é a que gerencia a execução,
depois das opções parseadas ela chama o run para cada Mode.

O Mode é um enum que tem dois valores: 
 1. server
 2. cliente


# **_message_**

Nele encontramos quase tudo relacionado a envio, recebimento e como lidar com as diferentes mensagens trocadas entre 
cliente e servidor:
 TODO
- Message
- MessageHandler
- Signin
- Type

# **_runners_**

Esses são de fato as classes que representam o cliente(client.Client) e o servidor(server.Server) partem de uma interface inicial chamada RunnerInterface.
O CommandExecuter serve para mandarmos executar comandos bash no cliente.

## A classe cliente

A classe cliente abre uma thread para ficar ouvindo mensagens do servidor como:  me dá o status desse serviço que está rodando, ou como está sua saude em relação a memória, cpu e disco.
Outra thread é para tempos em tempos enviar mensagem ao servidor mandado status dos serviços e dele. 

## A classe servidor
Ela abre duas threads uma para ouvir mensagens dos clientes. e a outra para mostrar na linha de comando os clientes dele e os status.
 

# **_utils_**
Possui classes utilitárias para facilitar encapsulamento de código.

A classe JSONUtils é responsável por deserializar uma string em formato json para uma classe válida.
E seriliazar uma classe em uma string json. Para esse quesito optei por usar uma biblioteca aplamente usada no mercado o [GSON](https://github.com/google/gson) 
