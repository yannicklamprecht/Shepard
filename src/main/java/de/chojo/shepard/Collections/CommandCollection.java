package de.chojo.shepard.Collections;

import de.chojo.shepard.modules.commands.Command;

import java.util.ArrayList;

public class CommandCollection {

    private static CommandCollection instance;

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

    private CommandCollection(){}

    private ArrayList<Command> commands = new ArrayList<Command>();

    public void addCommand(Command command) {
        commands.add(command);
    }


    public ArrayList<Command> getCommands() {
        return commands;
    }

    public Command getCommand(String command) {
        for (Command currentCommand : commands) {
            if (currentCommand.isCommand(command))
                return currentCommand;


        }
        return null;
    }

}
