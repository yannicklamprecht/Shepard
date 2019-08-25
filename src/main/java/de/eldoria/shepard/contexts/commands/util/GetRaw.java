package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A command for echoing messages in a raw format.
 */
public class GetRaw extends Command {

    /**
     * Creates a new get raw command object.
     */
    public GetRaw() {
        commandName = "getRaw";
        commandDesc = "Get the message in raw format";
    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        MessageSender.sendMessage("`" + receivedEvent.getMessage().getContentRaw() + "`", receivedEvent.getChannel());
    }
}
