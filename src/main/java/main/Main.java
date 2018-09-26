package main;

import cli.CliArgs;
import runners.RunnerInterface;
import runners.client.Client;
import picocli.CommandLine;
import runners.server.Server;


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
        RunnerInterface runner;
        if (cliArgs.isServer()){
            runner  = new Server(cliArgs);
        }else{
            runner  =  new Client(cliArgs);
        }
        runner.run();
    }
}
