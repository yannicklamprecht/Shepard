package de.eldoria.shepard.contexts.commands;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.CommandArgInfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

public class CommandInfo {
    private String contextName;
    private String name;
    private String[] aliases;
    private String description;
    private ContextCategory category;
    private List<CommandArgInfo> arguments;

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
        CommandArg[] commandArgs = command.getCommandArgs();

        arguments = Arrays.stream(commandArgs).map(CommandArgInfo::new)
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
