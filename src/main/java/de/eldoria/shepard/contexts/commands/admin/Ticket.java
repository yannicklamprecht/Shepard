package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static java.lang.System.lineSeparator;

public class Ticket extends Command {

    /**
     * Create ticket command object.
     */
    public Ticket() {
        commandName = "ticket";
        commandAliases = new String[] {"t"};
        commandDesc = "Ticket system for creation of channels to help users";
        arguments = new CommandArg[] {
                new CommandArg("action",
                        "**open** -> Open a new ticket" + lineSeparator()
                                + "**close** -> Close a ticket" + lineSeparator()
                                + "**showTypes** -> Shows a list of all available ticket types",
                        true),
                new CommandArg("value",
                        "**open** -> [ticket_type] [user_name]" + lineSeparator()
                                + "**close** -> Leave empty. Execute in channel which you want to close."
                                + lineSeparator()
                                + "**showTypes** -> Leave empty.",
                        false)};
    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String cmd = args[0];
        if (cmd.equalsIgnoreCase("open")) {
            return;
        }

        if (cmd.equalsIgnoreCase("close")) {
            return;
        }

        if (cmd.equalsIgnoreCase("showTypes")) {
            return;
        }
        MessageSender.sendSimpleError("Invalid Argument", receivedEvent.getChannel());
        sendCommandUsage(receivedEvent.getChannel());
    }
}
