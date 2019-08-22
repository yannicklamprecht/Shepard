package de.chojo.shepard.contexts.commands.admin;

import de.chojo.shepard.database.queries.Prefix;
import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.contexts.commands.Command;
import de.chojo.shepard.contexts.commands.CommandArg;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetPrefix extends Command {

    public SetPrefix() {
        commandName = "setPrefix";
        commandDesc = "Changes the prefix for the Server";
        arguments = new CommandArg[] {new CommandArg("Prefix", "The Prefix. Only one Char is allowed", true)};
    }

    @Override
    public boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        if (args[0].length() > 2) {
            Messages.sendSimpleError("Only one or two Chars are allowed as prefix.", receivedEvent.getChannel());
            return true;
        }

        Prefix.setPrefix(receivedEvent.getGuild().getId(), args[1].trim());
        Messages.sendMessage("Changed prefix to '" + args[1] + "'", receivedEvent.getChannel());
        return true;
    }
}
