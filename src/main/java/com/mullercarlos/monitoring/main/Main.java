package com.mullercarlos.monitoring.main;

import com.mullercarlos.monitoring.cli.CliArgs;
import picocli.CommandLine;

/**
 * Classe de entrada do programa
 */
public class Main {

    public static void main(String[] args) {
        var cliArgs = new CliArgs();
        /**
         * A lib parsei as opções de linha de comando e popula a classe com os args passados
         */
        new CommandLine(cliArgs).parse(args);
        //printa o
        if(cliArgs.isVerbose()) {
            System.out.println(cliArgs);
        }
        //se a usuário passou -h
        if(cliArgs.isUsageHelpRequested()){
            CommandLine.usage(new CliArgs(), System.out);
            return;
        }
        cliArgs.getMode().getRunner(cliArgs).run();
    }
}
