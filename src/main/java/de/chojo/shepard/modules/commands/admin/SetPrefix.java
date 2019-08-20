package de.chojo.shepard.modules.commands.admin;

import de.chojo.shepard.database.queries.Prefix;
import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.modules.commands.Command;
import de.chojo.shepard.modules.commands.CommandArg;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.management.Query;

public class SetPrefix extends Command {

    public SetPrefix() {
        commandName = "setPrefix";
        commandDesc = "Changes the prefix for the Server";
        args = new CommandArg[] {new CommandArg("Prefix", "The Prefix. Only one Char is allowed", true)};
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {
        if (args[1].length() > 2) {
            Messages.sendSimpleError("Only one or two Chars are allowed as prefix.", receivedEvent.getChannel());
            return true;
        }

        Prefix.setPrefix(receivedEvent.getGuild().getId(), args[1].trim());
        Messages.sendMessage("Changed prefix to '" + args[1] + "'", receivedEvent.getChannel());
        return true;
    }
}
