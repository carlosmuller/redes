package com.mullercarlos.monitoring.runners;

import com.mullercarlos.monitoring.cli.CliArgs;
import lombok.ToString;

@ToString
public abstract class RunnerInterface {
    protected int port;

    protected final CliArgs args;

    protected RunnerInterface(CliArgs args) {
        this.args = args;
        this.port = args.getPort();
    }

    public abstract void run();

}
