package com.mullercarlos.monitoring.runners;

import com.mullercarlos.monitoring.cli.CliArgs;
import com.mullercarlos.monitoring.message.MessageHandler;
import lombok.*;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

@ToString
public abstract class RunnerInterface {
    private boolean verbose;
    private int port;

    protected final CliArgs args;

    protected RunnerInterface(CliArgs args) {
        this.args = args;
        this.port = args.getPort();
        this.verbose = args.isVerbose();
    }

    public abstract void run();

    public int getPort() {
        return port;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public abstract MessageHandler getMessageHandler(Socket socket, String uuid) throws IOException;
}
