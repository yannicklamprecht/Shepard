package de.chojo.shepard.modules.commands.Fun;

import de.chojo.shepard.messageHandler.Messages;
import de.chojo.shepard.modules.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Uwud extends Command {
    public Uwud() {
        commandName = "uwud";
        commandAliases = null;
        commandDesc = "UWU and delete";
        args = null;
    }


    @Override
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {
        Messages.sendMessage(":regional_indicator_u::regional_indicator_w::regional_indicator_u:", receivedEvent.getChannel());
        Messages.deleteMessage(receivedEvent);
        return true;
    }
}
