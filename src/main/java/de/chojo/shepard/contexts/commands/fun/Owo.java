package de.chojo.shepard.contexts.commands.fun;

import de.chojo.shepard.messagehandler.MessageSender;
import de.chojo.shepard.contexts.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Owo extends Command {

    /**
     * Creates a new owo keyword object.
     */
    public Owo() {
        commandName = "owo";
        commandDesc = "OWO";
    }


    @Override
    public boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        MessageSender.sendMessage(":regional_indicator_o::regional_indicator_w::regional_indicator_o:",
                receivedEvent.getChannel());
        return true;
    }
}
