package de.chojo.shepard.contexts.commands;

import de.chojo.shepard.collections.CommandCollection;
import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.contexts.ContextSensitive;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;

/**
 * An abstract class for commands.
 */
public abstract class Command extends ContextSensitive {
    protected String commandName = "";
    protected String[] commandAliases = new String[0];
    protected String commandDesc = "";
    protected CommandArg[] arguments = new CommandArg[0];

    /**
     * Create a new command an register it to the {@link CommandCollection}.
     */
    protected Command() {
        CommandCollection.getInstance().addCommand(this);
    }

    /**
     * Executes the command.
     *
     * @param label         Label/Alias which was used for command execution
     * @param args          Arguments of the command.
     * @param receivedEvent Message Received Event of the command execution
     * @return True if the command was executed successful
     */
    public abstract boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent);

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
    public CommandArg[] getArguments() {
        if (arguments == null) {
            arguments = new CommandArg[0];
        }
        return arguments;
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

    public boolean checkArguments(String[] args) {
        int requiredArguments = 0;
        for (var a : arguments) {
            if (a.isRequired()) {
                requiredArguments++;
            }
        }
        return args.length >= requiredArguments;
    }

    public void sendCommandUsage(MessageChannel channel) {
        ArrayList<MessageEmbed.Field> fields = new ArrayList<>();

        fields.add(new MessageEmbed.Field("Description:", getCommandDesc(), false));

        String field = "";
        String desc = "";

        if (getCommandAliases() != null && getCommandAliases().length != 0) {
            field = field.concat("Aliases");
            for (String alias : getCommandAliases()) {
                desc = desc.concat(alias + " ");
            }
            fields.add(new MessageEmbed.Field(field, desc, false));
        }


        field = "Usage:";
        desc = "";

        desc = desc.concat(getCommandName() + " ");
        if (getArguments() != null) {
            for (CommandArg arg : getArguments()) {
                if (arg.isRequired()) {
                    desc = desc.concat("[" + arg.getArgName() + "] ");
                } else {
                    desc = desc.concat("<" + arg.getArgName() + "> ");
                }
            }
        }

        fields.add(new MessageEmbed.Field(field, desc, false));

        Messages.sendTextBox("Help for command " + getCommandName(), fields, channel);
    }

    public void sendCommandArgHelp(String argument, MessageChannel channel) {

        if (arguments == null || arguments.length == 0) {
            Messages.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("No Argument found!", "This command, doesn't have any arguments.", false)}, channel);
            return;
        }

        for (CommandArg arg : arguments) {
            if (arg.getArgName().equalsIgnoreCase(argument)) {
                ArrayList<MessageEmbed.Field> fields = new ArrayList<>();
                fields.add(new MessageEmbed.Field("Description:", arg.getArgDesc(), false));
                fields.add(new MessageEmbed.Field("Required", arg.isRequired().toString(), false));
                Messages.sendTextBox("Help for Argument: \"" + arg.getArgName() + "\" of command \"" + getCommandName() + "\"", fields, channel);
                return;
            }
        }

        String argsAsString = "";
        for (CommandArg arg : arguments) {
            argsAsString = argsAsString.concat(arg.getArgName() + " ");
        }
        Messages.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("Argument not found!", "Try one of these: " + argsAsString, false)}, channel);
    }
}