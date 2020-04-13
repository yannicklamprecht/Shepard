package de.eldoria.shepard.contexts.commands.argument;

import de.eldoria.shepard.localization.util.TextLocalizer;

/**
 * Class to save a {@link SubCommand} for serialization.
 */
public class SubCommandInfo {
    private final String commandDescription;
    private final ParameterInfo[] parameter;

    /**
     * Creates a new Command arg info object.
     *
     * @param commandDescription description of sub command.
     * @param parameter parameter info array
     */
    public SubCommandInfo(String commandDescription, ParameterInfo[] parameter) {
        this.commandDescription = TextLocalizer.localizeAllAndReplace(commandDescription, null);
        this.parameter = parameter;
    }
}
