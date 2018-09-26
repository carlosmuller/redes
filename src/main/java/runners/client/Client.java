package runners.client;

import cli.CliArgs;
import lombok.*;
import runners.RunnerInterface;

@ToString(callSuper = true)
public class Client extends RunnerInterface {

    public Client(CliArgs args) {
        super(args);
    }

    @Override
    public void run() {
        System.out.println(this.toString());
    }
}
