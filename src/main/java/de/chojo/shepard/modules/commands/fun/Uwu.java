package de.chojo.shepard.modules.commands.fun;

import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.modules.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Uwu extends Command {

    public Uwu() {
        super("uwu", "UWU");
    }


    @Override
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {
        Messages.sendMessage(":regional_indicator_u::regional_indicator_w::regional_indicator_u:", receivedEvent.getChannel());
        return true;
    }
}
