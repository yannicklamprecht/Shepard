package de.eldoria.shepard.basemodules.commanddispatching.util;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.SubCommand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class CommandSearchResult {
    private static final CommandSearchResult EMPTY = new CommandSearchResult(null, null);
    private final Command command;
    private final SubCommand subCommand;

    /**
     * Creates a new command and subcommand search result.
     *
     * @param command    command which was found
     * @param subCommand subcommand which was found
     */
    public CommandSearchResult(@Nullable Command command, @Nullable SubCommand subCommand) {
        this.command = command;
        this.subCommand = subCommand;
        if (command == null && subCommand != null) {
            throw new IllegalArgumentException("Command cant be null if a subcommand is provided.");
        }
    }

    /**
     * Create a new command search result without a subcommand.
     *
     * @param command command which was found
     */
    public CommandSearchResult(@Nullable Command command) {
        this.command = command;
        this.subCommand = null;
    }

    /**
     * Get a optional command of the result.
     *
     * @return optional command
     */
    @Nonnull
    public Optional<Command> command() {
        return Optional.ofNullable(command);
    }

    /**
     * Get a optional subcommand of the result. Subcommand is never present, if no command is present.
     *
     * @return optional subcommand
     */
    @Nonnull
    public Optional<SubCommand> subCommand() {
        return Optional.ofNullable(subCommand);
    }

    /**
     * Get a empty search result.
     *
     * @return static empty result
     */
    public static CommandSearchResult empty() {
        return EMPTY;
    }

    /**
     * Checks if {@link #subCommand()} and {@link #command()} are not present.
     *
     * @return true if both are not present or if no command is present
     */
    public boolean isEmpty() {
        return command().isEmpty();
    }

    /**
     * Get the identifier of the search result.
     * This can be  the command or the command and subcommand identifier separated by a ".".
     *
     * @return identifier of search result.
     */
    public String getIdentifier() {
        if (command().isPresent() && subCommand().isPresent()) {
            return command().get().getCommandIdentifier() + "." + subCommand().get().getSubCommandIdentifier();
        }

        if (command().isPresent()) {
            return command().get().getCommandIdentifier();
        }
        throw new RuntimeException("No identifier could be returned. No command or subcommand is present!");
    }
}
