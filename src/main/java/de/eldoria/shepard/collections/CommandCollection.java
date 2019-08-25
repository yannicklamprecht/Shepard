package de.chojo.shepard.collections;

import de.chojo.shepard.contexts.commands.Command;

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
     * Prints a debug message for all commands to console.
     */
    public void debug() {
        System.out.println("++++ DEBUG OF COMMANDS ++++");
        for (Command c : commands) {
            c.printDebugInfo();
        }
    }
}
