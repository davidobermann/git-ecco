package at.jku.isse.gitecco.ecco;

/**
 * Interface to implement different ecco commands.
 */
public interface EccoCommand {
    /**
     * Produces and returnes the command for ecco
     * @return ecco command message as String
     */
    public String getCommandMsg();
}
