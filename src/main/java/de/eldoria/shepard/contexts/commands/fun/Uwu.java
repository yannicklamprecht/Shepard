package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Uwu extends Command {

    /**
     * Creates new uwu command object.
     */
    public Uwu() {
        commandName = "uwu";
        commandDesc = "UWU";
    }


    @Override
    public void internalExecute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        MessageSender.sendMessage(":regional_indicator_u::regional_indicator_w::regional_indicator_u:",
                receivedEvent.getChannel());
    }
}
