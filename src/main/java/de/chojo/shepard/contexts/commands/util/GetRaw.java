package de.chojo.shepard.contexts.commands.util;

import de.chojo.shepard.messagehandler.MessageSender;
import de.chojo.shepard.contexts.commands.Command;
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
    public boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        MessageSender.sendMessage("`" + receivedEvent.getMessage().getContentRaw() + "`", receivedEvent.getChannel());
        return true;
    }
}
