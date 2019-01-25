package de.chojo.shepard.modules.commands.Fun;

import de.chojo.shepard.Messages;
import de.chojo.shepard.modules.commands.Command;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Owod extends Command {
    public Owod() {
        commandName = "owod";
        commandAliases = null;
        commandDesc = "OWO and delete";
        args = null;
    }


    @Override
    public boolean execute(String[] args, MessageChannel channel, MessageReceivedEvent receivedEvent) {
        Messages.sendMessage(":regional_indicator_o::regional_indicator_w::regional_indicator_o:", channel);
        Messages.deleteMessage(receivedEvent);
        return true;
    }
}
