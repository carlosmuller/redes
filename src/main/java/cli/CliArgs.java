package cli;

import lombok.*;
import main.Mode;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import static main.Mode.server;

@ToString
@EqualsAndHashCode
@Data
@CommandLine.Command(headerHeading = "Um programa para monitorar recursos e serviços\n",requiredOptionMarker = '*', abbreviateSynopsis = true)
public class CliArgs {

    @Option(names = {"-v", "--verbose"}, defaultValue = "false", description = "Mostra mais mensagens de log (default: ${DEFAULT-VALUE})")
    private boolean verbose = false;

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Mostra essa ajuda :)")
    boolean usageHelpRequested;


    @Option(names = {"-m", "--mode"}, required = true, description = "Modos: ${COMPLETION-CANDIDATES}")
    private Mode mode;

    @Option(names = {"-p", "--port"}, required = true, description = "numero da porta")
    private int port;

    @Option(names = {"-s", "--server"}, description = "servidor e porta para se comunicar 123.112.132.123:8080")
    private String server;

    public boolean isServer() {
        return server.equals(this.mode);
    }

}
