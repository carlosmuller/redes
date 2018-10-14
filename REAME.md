ESCOLA DE ARTES, CIÊNCIAS E HUMANIDADES – EACH – USP
Disciplina: ACH2026 - Redes de Computadores Turma: 04
Professor: João Luiz Bernardes Junior 
Atividade:  Ep1 - Programando com sockets  
Alunos: 8061473 - Carlos Eduardo Müller de Carvalho  Turma: 04

link para o docs https://docs.google.com/document/d/1MFEZxLE6QbFsAXsKt_z_i9jfwajU2_9qFaIsreTNYkg/edit?usp=sharing

#Compilação
## O que precisa para compilar?
  
  - [Java 11](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
  - [Maven 3.5+](https://maven.apache.org/download.cgi) - [Ubuntu](sudo apt-get install maven)
  
## Como compilar
Basta rodar na raiz do projeto:
  - `mvn package`
O arquivo final para rodar o projeto estará em `target/monitoring-1.0-SNAPSHOT-jar-with-dependencies.jar`    

# Codar
Para codar basta baixar sua ide favorita e importar como maven project.

Para facilitar algumas coisas eu uso o [lombok](https://projectlombok.org/),
para sua ide reconhecer os métodos gerados por ele instale o plugin [how to](https://projectlombok.org/setup/overview)  

# Rodar


Para ver as opções do programa:
```
java -jar monitoring-1.0-SNAPSHOT-jar-with-dependencies.jar -h
```



Para rodar em modo servidor:

```
java -jar monitoring-1.0-SNAPSHOT-jar-with-dependencies.jar -m=server -p=8080
```

Para rodar em modo cliente:

```
java -jar monitoring-1.0-SNAPSHOT-jar-with-dependencies.jar -m=client -p=8080 -s=192.168.1.123:8080 -a=authKey
```

Tanto o servidor como o cliente escutam mensagens por isso precisa da opção `[-p|--port]`.

## Mais informações
Para melhor documentação vá para [docs](docs/PacotesEClasses.md)

