package de.chojo.shepard.contexts.commands.util;

import de.chojo.shepard.contexts.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A test command without specified behaviour.
 */
public class Test extends Command {

    public Test() {
        commandName = "test";
        commandAliases = new String[]{};
        commandDesc = "Testcommand!";
        arguments = null;
    }

    @Override
    public boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        return true;
    }
}
