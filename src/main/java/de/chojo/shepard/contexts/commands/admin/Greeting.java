package de.chojo.shepard.contexts.commands.admin;

import de.chojo.shepard.contexts.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Greeting extends Command {
    @Override
    public boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        return false;
    }
}
