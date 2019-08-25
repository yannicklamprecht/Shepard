package de.chojo.shepard.contexts.commands.admin;

import de.chojo.shepard.database.queries.Prefix;
import de.chojo.shepard.messagehandler.MessageSender;
import de.chojo.shepard.contexts.commands.Command;
import de.chojo.shepard.contexts.commands.CommandArg;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Deprecated
public class SetPrefix extends Command {

    /**
     * Creates a new setprefix command.
     */
    public SetPrefix() {
        commandName = "setPrefix";
        commandDesc = "Changes the prefix for the Server";
        arguments = new CommandArg[] {new CommandArg("Prefix", "The Prefix. Only one Char is allowed", true)};
    }

    @Override
    public boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        if (args[0].length() > 2) {
            MessageSender.sendSimpleError("Only one or two Chars are allowed as prefix.", receivedEvent.getChannel());
            return true;
        }

        Prefix.setPrefix(receivedEvent.getGuild(), args[1].trim(), receivedEvent);
        MessageSender.sendMessage("Changed prefix to '" + args[1] + "'", receivedEvent.getChannel());
        return true;
    }
}
