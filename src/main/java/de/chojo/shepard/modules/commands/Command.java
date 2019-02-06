package de.chojo.shepard.modules.commands;

import de.chojo.shepard.collections.CommandCollection;
import de.chojo.shepard.modules.ContextSensitive;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * An abstract class for commands.
 */
public abstract class Command extends ContextSensitive {
    protected String commandName;
    protected String[] commandAliases;
    protected String commandDesc;
    protected CommandArg[] args;

    /**
     * Create a new command an register it to the {@link CommandCollection}.
     * With this constructor, no aliases are set.
     * @param commandName the command name.
     * @param commandDescription the command description.
     * @param args the command arguments.
     */
    protected Command(String commandName, String commandDescription, CommandArg... args) {
        this(commandName, new String[0], commandDescription, args);
    }

    /**
     * Create a new command an register it to the {@link CommandCollection}.
     * @param commandName the command name.
     * @param commandAliases the command aliases.
     * @param commandDescription the command description.
     * @param args the command arguments.
     */
    protected Command(String commandName, String[] commandAliases, String commandDescription, CommandArg... args){
        this.commandName = commandName;
        this.commandAliases = commandAliases;
        this.commandDesc = commandDescription;
        this.args = args;
        CommandCollection.getInstance().addCommand(this);
    }

    /**
     * Executes the command with given argument parameters for a specific {@link MessageReceivedEvent}.
     * @param args the arguments provided by the command sender.
     * @param receivedEvent the message event called with the sent command.
     * @return {@code true} if the command was executed successfully, {@code false} otherwise.
     */
    public abstract boolean execute(String[] args, MessageReceivedEvent receivedEvent);

    /**
     * Get the name of the command.
     *
     * @return the name of the command.
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Get the description of the command.
     *
     * @return the description
     */
    public String getCommandDesc() {
        return commandDesc;
    }


    /**
     * Get all possible arguments for the command.
     *
     * @return an array of command arguments.
     */
    public CommandArg[] getArgs() {
        if (args == null) {
            args = new CommandArg[0];
        }
        return args;
    }

    /**
     * Get possible aliases of a command.
     *
     * @return an array of aliases.
     */
    public String[] getCommandAliases() {
        return commandAliases;
    }

    /**
     * Check whether a string is a valid command or not.
     *
     * @param command the string to check.
     * @return {@code true} if the command matched, {@code false} otherwise.
     */
    public boolean isCommand(String command) {
        if (command.equalsIgnoreCase(this.commandName)) return true;
        if (this.commandAliases != null) {
            for (String alias : this.commandAliases) {
                if (command.equalsIgnoreCase(alias)) return true;
            }
        }
        return false;
    }
}