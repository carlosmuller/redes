package com.mullercarlos.main;

import com.mullercarlos.cli.CliArgs;
import picocli.CommandLine;


public class Main {

    public static void main(String[] args) {
        var cliArgs = new CliArgs();

        new CommandLine(cliArgs).parse(args);
        if(cliArgs.isVerbose()) {
            System.out.println(cliArgs);
        }
        if(cliArgs.isUsageHelpRequested()){
            CommandLine.usage(new CliArgs(), System.out);
            return;
        }
        cliArgs.getMode().getRunner(cliArgs).run();
    }
}
