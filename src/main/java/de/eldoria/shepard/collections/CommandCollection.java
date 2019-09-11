package de.eldoria.shepard.collections;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.Command;
import info.debatty.java.stringsimilarity.JaroWinkler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public Command getSimilarCommand(String command) {
        double score = 0;
        Command cmd = null;
        for (Command currentCommand : commands) {
            double similarityScore = currentCommand.getSimilarityScore(command);
            if (similarityScore > score) {
                score = similarityScore;
                cmd = currentCommand;
            }
        }
        if (score > 0.75) {
            return cmd;
        }
        return null;
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
}
