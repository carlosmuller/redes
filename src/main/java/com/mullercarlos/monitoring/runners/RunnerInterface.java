package com.mullercarlos.monitoring.runners;

import com.mullercarlos.monitoring.main.CliArgs;
import com.mullercarlos.monitoring.message.MessageHandler;
import lombok.ToString;

import java.io.IOException;
import java.net.Socket;

@ToString
public abstract class RunnerInterface {
    protected final CliArgs args;
    private boolean verbose;
    private int port;

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
