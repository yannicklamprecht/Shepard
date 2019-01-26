package de.chojo.shepard.modules.commands;

import de.chojo.shepard.collections.CommandCollection;
import de.chojo.shepard.modules.ContextSensitive;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Command extends ContextSensitive {
    protected String commandName;
    protected String[] commandAliases;
    protected String commandDesc;
    protected CommandArg[] args;

    public String getCommandName() {
        return commandName;
    }

    public String getCommandDesc() {
        return commandDesc;
    }


    public CommandArg[] getArgs() {
        if (args == null) {
            args = new CommandArg[0];
        }
        return args;
    }

    protected Command(){
        CommandCollection.getInstance().addCommand(this);
    }


    public String[] getCommandAliases() {
        return commandAliases;
    }


    /* */
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {
        return false;
    }

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