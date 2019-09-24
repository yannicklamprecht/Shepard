package de.eldoria.shepard.collections;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class CommandCollection {
    private static CommandCollection instance;
    private final List<Command> commands = new ArrayList<>();

    private CommandCollection() {
    }

    /**
     * Gets the Command Collection Instance.
     *
     * @return Instance of Command Collection
     */
    public static CommandCollection getInstance() {
        if (instance == null) {
            synchronized (CommandCollection.class) {
                if (instance == null) {
                    instance = new CommandCollection();
                }
            }
        }
        return instance;
    }

    /**
     * Adds a Command to the Command Collection.
     *
     * @param command Command object to add.
     */
    public void addCommand(Command command) {
        commands.add(command);
    }


    /**
     * Get a list of all registered commands.
     *
     * @return unmodifiable list
     */
    public List<Command> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    /**
     * Get a command object by command name or alias.
     *
     * @param command command name or alias
     * @return Command object or null if no command was found.
     */
    public Command getCommand(String command) {
        for (Command currentCommand : commands) {
            if (currentCommand.isCommand(command)) {
                return currentCommand;
            }
        }
        return null;
    }

    /**
     * Returns a command which have the most similar name or alias.
     *
     * @param command command entered.
     * @return Command or null if no command was found which was similar enough.
     */
    public List<Command> getSimilarCommands(String command) {
        List<RankedCommand> rankedCommands = new ArrayList<>();
        for (Command currentCommand : commands) {
            double similarityScore = currentCommand.getSimilarityScore(command);
            if (similarityScore > 0.75) {
                rankedCommands.add(new RankedCommand(similarityScore, currentCommand));
            }
        }

        Collections.sort(rankedCommands);
        return rankedCommands.stream().map(rankedCommand -> rankedCommand.command).collect(Collectors.toList());
    }

    /**
     * Prints a debug message for all commands to console.
     */
    public void debug() {
        ShepardBot.getLogger().info("++++ DEBUG OF COMMANDS ++++");
        for (Command c : commands) {
            c.printDebugInfo();
        }
    }

    static class RankedCommand implements Comparable<RankedCommand> {
        /**
         * Rank of the command.
         */
        final double rank;
        /**
         * Command object which is ranked.
         */
        final Command command;

        RankedCommand(double rank, Command command) {
            this.rank = rank;
            this.command = command;
        }


        @Override
        public int compareTo(@NotNull CommandCollection.RankedCommand cmd) {
            return Double.compare(rank, cmd.rank);
        }
    }
}
