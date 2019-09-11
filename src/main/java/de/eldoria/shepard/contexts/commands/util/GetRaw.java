package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;

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
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper dataWrapper) {
        MessageSender.sendMessage("`" + dataWrapper.getMessage().getContentRaw() + "`", dataWrapper.getChannel());
    }
}
