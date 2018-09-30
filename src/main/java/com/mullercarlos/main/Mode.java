package com.mullercarlos.main;

import com.mullercarlos.cli.CliArgs;
import com.mullercarlos.runners.RunnerInterface;
import com.mullercarlos.runners.client.Client;
import com.mullercarlos.runners.server.Server;

public enum Mode {
    server(){
        @Override
        public RunnerInterface getRunner(CliArgs args) {
           return new Server(args);
        }
    }, client(){
        @Override
        public RunnerInterface getRunner(CliArgs args) {
            return new Client(args);
        }
    };

    public RunnerInterface getRunner(CliArgs args){
        throw new IllegalStateException("O modo novo deve implementar o run");
    }
}
