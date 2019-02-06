package de.chojo.shepard.modules.commands.fun;

import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.modules.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Owo extends Command {

    public Owo() {
        commandName = "owo";
        commandAliases = null;
        commandDesc = "OWO";
        args = null;
    }


    @Override
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {
        Messages.sendMessage(":regional_indicator_o::regional_indicator_w::regional_indicator_o:", receivedEvent.getChannel());
        return true;
    }
}
