package de.eldoria.shepard.webapi.apiobjects.commandserialization;

import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.util.TextLocalizer;
import lombok.Data;

/**
 * Class for serialization of a command.
 */
@Data
public class FullCommandInfo {
    private final String commandIdentifier;
    private final String commandName;
    private final String[] aliases;
    private final String description;
    private final String standaloneDescription;
    private final CommandCategory category;
    private final SubCommandInfo[] subCommands;

    /**
     * Create a new command info.
     *
     * @param commandIdentifier     name of context
     * @param commandName           name of command
     * @param commandAliases        aliases of command
     * @param commandDesc           description of command
     * @param standaloneDescription standalone description of the command
     * @param category              category of command
     * @param subCommands           subcommands of command
     */
    public FullCommandInfo(String commandIdentifier, String commandName, String[] commandAliases,
                           String commandDesc, String standaloneDescription, CommandCategory category,
                           SubCommandInfo[] subCommands) {
        this.commandIdentifier = commandIdentifier;
        this.commandName = commandName;
        aliases = commandAliases.length == 0 ? null : commandAliases;
        this.description = TextLocalizer.localizeAllAndReplace(commandDesc, null);
        this.standaloneDescription = standaloneDescription != null
                ? TextLocalizer.localizeAllAndReplace(standaloneDescription, null) : null;
        this.category = category;
        this.subCommands = subCommands;
    }

    /**
     * Get the category of the command info.
     *
     * @return command category
     */
    public CommandCategory getCategory() {
        return category;
    }
}
