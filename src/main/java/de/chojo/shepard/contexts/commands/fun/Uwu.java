package de.chojo.shepard.contexts.commands.fun;

import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.contexts.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Uwu extends Command {

    public Uwu() {
        commandName = "uwu";
        commandAliases = null;
        commandDesc = "UWU";
        arguments = null;
    }


    @Override
    public boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        Messages.sendMessage(":regional_indicator_u::regional_indicator_w::regional_indicator_u:", receivedEvent.getChannel());
        return true;
    }
}
