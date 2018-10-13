package com.mullercarlos.monitoring.main;

import com.mullercarlos.monitoring.cli.CliArgs;
import com.mullercarlos.monitoring.runners.RunnerInterface;
import com.mullercarlos.monitoring.runners.client.Client;
import com.mullercarlos.monitoring.runners.server.Server;

/**
 * Enum responsavel para representação dos modos de execução do programa
 */
public enum Mode {
    server() {
        @Override
        public RunnerInterface getRunner(CliArgs args) {
            return new Server(args);
        }
    }, client() {
        @Override
        public RunnerInterface getRunner(CliArgs args) {
            return new Client(args);
        }
    };

    public RunnerInterface getRunner(CliArgs args) {
        throw new IllegalStateException("O modo novo deve implementar o run");
    }
}
