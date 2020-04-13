package de.eldoria.shepard.contexts.commands;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.argument.ParameterInfo;
import de.eldoria.shepard.contexts.commands.argument.SubCommandInfo;
import de.eldoria.shepard.localization.util.TextLocalizer;

/**
 * Class for serialization of a command.
 */
public class CommandInfo {
    private String contextName;
    private String name;
    private String[] aliases;
    private final String description;
    private ContextCategory category;
    private final SubCommandInfo[] subCommands;

    public CommandInfo(String contextName, String commandName, String[] commandAliases,
                       String commandDesc, ContextCategory category, SubCommandInfo[] subCommands) {
        this.contextName = contextName;
        name = commandName;
        aliases = commandAliases;
        this.description = TextLocalizer.localizeAllAndReplace(commandDesc, null);
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
