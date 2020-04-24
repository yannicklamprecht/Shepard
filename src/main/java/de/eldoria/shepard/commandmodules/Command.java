package de.eldoria.shepard.commandmodules;

import de.eldoria.shepard.basemodules.commanddispatching.CommandHub;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.LanguageHandler;
import de.eldoria.shepard.webapi.apiobjects.commandserialization.FullCommandInfo;
import de.eldoria.shepard.webapi.apiobjects.commandserialization.SubCommandInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.eldoria.shepard.commandmodules.CommandUtil.generateLazySubCommands;

/**
 * An abstract class for commands.
 */
@Slf4j
@Getter
public abstract class Command {
    /**
     * Language handler instance.
     */
    protected final LanguageHandler locale;
    /**
     * Name of the command.
     */
    protected final String commandName;
    /**
     * Command aliase as string array.
     */
    protected final String[] commandAliases;
    /**
     * Description of command.
     */
    protected final String commandDesc;
    /**
     * Command args as command arg array.
     */
    protected final SubCommand[] subCommands;
    /**
     * True if the plugin works without a subcommand.
     */
    protected final boolean standalone;
    /**
     * Description if the plugin has a standalone function.
     */
    protected final String standaloneDescription;
    /**
     * Category of the context.
     */
    protected CommandCategory category;

    /**
     * Create a new command with a standalone function and register it to the {@link CommandHub}.
     *
     * @param commandName           name of the command
     * @param commandAliases        command aliases
     * @param commandDesc           description of the command
     * @param subCommands           Subcommands of the command
     * @param standaloneDescription description of the standalone command
     * @param category              category of the command
     */
    protected Command(String commandName, String[] commandAliases, String commandDesc, SubCommand[] subCommands,
                      String standaloneDescription, CommandCategory category) {
        locale = LanguageHandler.getInstance();
        this.commandName = commandName;
        this.commandAliases = commandAliases == null ? new String[0] : commandAliases;
        this.commandDesc = commandDesc;
        this.subCommands = (subCommands == null ? new SubCommand[0] : subCommands);
        this.standalone = true;
        this.standaloneDescription = standaloneDescription;
        this.category = category;

        if (commandName == null || commandDesc == null || standaloneDescription == null) {
            throw new NullPointerException();
        }
        generateLazySubCommands(this.subCommands);
    }

    /**
     * Create a new command without a standalone function and register it to the {@link CommandHub}.
     *
     * @param commandName    name of the command
     * @param commandAliases command aliases
     * @param commandDesc    description of the command
     * @param subCommands    Subcommands of the command
     * @param category       category of the command
     */
    protected Command(String commandName, String[] commandAliases, String commandDesc, SubCommand[] subCommands,
                      CommandCategory category) {
        locale = LanguageHandler.getInstance();
        this.commandName = commandName;
        this.commandAliases = commandAliases == null ? new String[0] : commandAliases;
        this.commandDesc = commandDesc;
        this.subCommands = subCommands == null ? new SubCommand[0] : subCommands;
        this.standalone = false;
        this.standaloneDescription = null;
        this.category = category;

        if (commandName == null || commandDesc == null) {
            throw new NullPointerException();
        }
        generateLazySubCommands(this.subCommands);
    }

    /**
     * Create a new command with only a standalone function and register it to the {@link CommandHub}.
     *
     * @param commandName    name of the command
     * @param commandAliases command aliases
     * @param commandDesc    description of the command
     * @param category       category of the command
     */
    protected Command(String commandName, String[] commandAliases, String commandDesc,
                      CommandCategory category) {
        locale = LanguageHandler.getInstance();
        this.commandName = commandName;
        this.commandAliases = commandAliases == null ? new String[0] : commandAliases;
        this.commandDesc = commandDesc;
        this.subCommands = new SubCommand[0];
        this.standalone = true;
        this.standaloneDescription = null;
        this.category = category;

        if (commandName == null || commandDesc == null) {
            throw new NullPointerException();
        }
        generateLazySubCommands(this.subCommands);
    }

    /**
     * Check whether a string is a valid command or not.
     *
     * @param command the string to check.
     * @return {@code true} if the command matched, {@code false} otherwise.
     */
    public boolean isCommand(String command) {
        if (command.equalsIgnoreCase(this.commandName) || command.equalsIgnoreCase(getClass().getSimpleName())) {
            return true;
        }

        if (this.commandAliases != null) {
            for (String alias : this.commandAliases) {
                if (command.equalsIgnoreCase(alias)) return true;
            }
        }
        return false;
    }

    /**
     * Checks if enough arguments are present for the comment.
     *
     * @param args string arg array
     * @return true if enough arguments are present
     */
    public boolean checkArguments(String[] args) {
        // Check if command is standalone
        if (standalone) return true;

        return getMatchingSubcommand(args).isPresent();
    }

    /**
     * Search for the matching subcommand.
     * @param args args for subcommand matching
     * @return optional subcommand.
     */
    public Optional<SubCommand> getMatchingSubcommand(String[] args) {
        // search in subcommands
        for (SubCommand command : subCommands) {
            if (command.matchArgs(args)) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }

    /**
     * Get a object which holds all information about the command.
     *
     * @return command info object
     */
    public FullCommandInfo getCommandInfo() {
        SubCommandInfo[] sc = new SubCommandInfo[subCommands.length];
        return new FullCommandInfo(getCommandIdentifier(),
                commandName,
                commandAliases,
                commandDesc,
                standaloneDescription,
                category,
                Arrays.stream(subCommands)
                        .map(SubCommand::getSubCommandInfo)
                        .collect(Collectors.toList()).toArray(sc));
    }

    /**
     * Check if the first sub command matches a string.
     *
     * @param cmd command to check
     * @param i   subcommand index to check
     * @return true if the subcommand matches.
     */
    protected boolean isSubCommand(String cmd, int i) {
        return subCommands[i].isSubCommand(cmd);
    }

    /**
     * Get the context name.
     *
     * @return context name
     */
    public String getCommandIdentifier() {
        return getClass().getSimpleName();
    }
}
