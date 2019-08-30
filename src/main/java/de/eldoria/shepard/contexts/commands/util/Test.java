package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A test command without specified behaviour.
 */
public class Test extends Command {

    public Test() {
        commandName = "test";
        commandDesc = "Testcommand!";
    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        MessageSender.sendMessage("Echo", receivedEvent.getChannel());
    }
}
