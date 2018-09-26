package runners;

import cli.CliArgs;
import lombok.ToString;

@ToString
public abstract class RunnerInterface {

    protected final CliArgs args;

    protected RunnerInterface(CliArgs args) {
        this.args = args;
    }

    public abstract void run();

}
