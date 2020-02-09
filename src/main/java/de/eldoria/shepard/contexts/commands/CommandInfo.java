package de.eldoria.shepard.contexts.commands;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.argument.CommandArgument;
import de.eldoria.shepard.contexts.commands.argument.CommandArgumentInfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

/**
 * Class for serialization of a command.
 */
public class CommandInfo {
    private String contextName;
    private String name;
    private String[] aliases;
    private String description;
    private ContextCategory category;
    private List<CommandArgumentInfo> arguments;

    /**
     * Creates a new CommandInfo object.
     *
     * @param command command for information retrieval
     */
    public CommandInfo(Command command) {
        if (command == null) {
            return;
        }
        contextName = command.getContextName();
        name = command.getCommandName();
        aliases = command.getCommandAliases();
        description = localizeAllAndReplace(command.getCommandDesc(), null);
        category = command.getCategory();
        CommandArgument[] commandArguments = command.getCommandArguments();

        arguments = Arrays.stream(commandArguments).map(CommandArgumentInfo::new)
                .collect(Collectors.toList());
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
