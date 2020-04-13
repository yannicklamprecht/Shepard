package de.eldoria.shepard.contexts.commands;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.argument.SubCommandInfo;
import de.eldoria.shepard.localization.util.TextLocalizer;

/**
 * Class for serialization of a command.
 */
public class CommandInfo {
    private final String description;
    private final SubCommandInfo[] subCommands;
    private String contextName;
    private String name;
    private String[] aliases;
    private String standaloneDescription;
    private ContextCategory category;

    /**
     * Create a new command info.
     * @param contextName name of context
     * @param commandName name of command
     * @param commandAliases aliases of command
     * @param commandDesc description of command
     * @param standaloneDescription standalone description of the command
     * @param category category of command
     * @param subCommands subcommands of command
     */
    public CommandInfo(String contextName, String commandName, String[] commandAliases,
                       String commandDesc, String standaloneDescription, ContextCategory category,
                       SubCommandInfo[] subCommands) {
        this.contextName = contextName;
        name = commandName;
        aliases = commandAliases;
        this.description = TextLocalizer.localizeAllAndReplace(commandDesc, null);
        this.standaloneDescription = standaloneDescription;
        this.category = category;
        this.subCommands = subCommands;
    }

    /**
     * Get the category of the command info.
     *
     * @return command category
     */
    public ContextCategory getCategory() {
        return category;
    }
}
