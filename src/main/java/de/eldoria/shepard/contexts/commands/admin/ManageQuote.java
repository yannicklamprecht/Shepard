package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ManageQuote extends Command {

    ManageQuote() {
        commandName = "quote";
        commandDesc = "add or remove quotes";

    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {

    }
}
