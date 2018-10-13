package com.mullercarlos.monitoring.main;

import com.mullercarlos.monitoring.main.Mode;
import lombok.*;
import picocli.CommandLine;
import picocli.CommandLine.Option;

/**
 * Classe respnsável por representar as opções aceitas na linha de comando
 */
@ToString//gera o to string com os atributos da classe
@EqualsAndHashCode//gera o equals e hash code
@Data// adicona getters and setters para atributos em tempo de compilação
@CommandLine.Command(headerHeading = "Um programa para monitorar recursos e serviços\n", requiredOptionMarker = '*', abbreviateSynopsis = true)
public class CliArgs {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Mostra essa ajuda :)")
    boolean usageHelpRequested;
    @Option(names = {"-v", "--verbose"}, defaultValue = "false", description = "Mostra mais mensagens de log (default: ${DEFAULT-VALUE})")
    private boolean verbose = false;
    @Option(names = {"-m", "--mode"}, required = true, description = "Modos: ${COMPLETION-CANDIDATES}")
    private Mode mode;

    @Option(names = {"-p", "--port"}, required = true, description = "numero da porta")
    private int port;

    @Option(names = {"-s", "--server"}, description = "servidor e porta para se comunicar 123.112.132.123:8080")
    private String server;

    @Option(names = {"-a", "--auth-key"}, description = "chave da api para o cliente mandar pro servidor(gerada pela opção 1 do servidor)")
    private String authKey;

}
