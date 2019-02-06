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
     */
    protected Command(){
        CommandCollection.getInstance().addCommand(this);
    }

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