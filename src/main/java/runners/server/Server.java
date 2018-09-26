package runners.server;

import cli.CliArgs;
import lombok.*;
import runners.RunnerInterface;

@ToString(callSuper = true)
public class Server extends RunnerInterface {

    public Server(CliArgs args) {
        super(args);
    }

    @Override
    public void run() {
        System.out.println(this);

    }
}
