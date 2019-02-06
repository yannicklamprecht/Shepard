package de.chojo.shepard.modules.commands.util;

import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.modules.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A command for echoing messages in a raw format.
 */
public class GetRaw extends Command {

    public GetRaw(){
        super("getRaw", "Get the message in raw format");
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {
        Messages.sendMessage("`" + receivedEvent.getMessage().getContentRaw() + "`", receivedEvent.getChannel());
        return true;
    }
}
