package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static java.lang.System.lineSeparator;

public class TicketSettings extends Command {
    /**
     * Creates a new ticket setting command object.
     */
    public TicketSettings() {
        commandName = "ticketSettings";
        commandDesc = "Manage Ticket settings";
        commandAliases = new String[] {"ts"};
        arguments = new CommandArg[] {
                new CommandArg("action",
                        "**createType** -> Creates a new ticket type" + lineSeparator()
                                + "**removeType** -> Removes a ticket type by type name or id" + lineSeparator()
                                + "**setOwnerRoles** -> Sets ticket owner roles for ticket type" + lineSeparator()
                                + "**setSupportRoles** -> Sets ticket support roles for ticket type" + lineSeparator()
                                + "**setCreationMessage** -> Sets the creation Message of the ticket type" + lineSeparator()
                                + "Message will be send when a ticket is created.",
                        true),
                new CommandArg("value",
                        "**createType** -> [type_name] [channel_category]" + lineSeparator()
                                + "**removeType** -> [type_name]" + lineSeparator()
                                + "**setOwnerRoles** -> [type_name] [Roles...] One or more Roles" + lineSeparator()
                                + "**setSupportRoles** -> [type_name] [Roles...] One or more Roles" + lineSeparator()
                                + "**setCreation Message** -> [type_names] [Message]" + lineSeparator()
                                + "Supported Placeholder: {user_name} {user_tag} {user_mention}",
                        true)};

    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String cmd = args[0];
        if (cmd.equalsIgnoreCase("createType")) {

            return;
        }

        if (cmd.equalsIgnoreCase("removeType")) {

            return;
        }

        if (cmd.equalsIgnoreCase("setOwnerRoles")) {
            return;
        }

        if (cmd.equalsIgnoreCase("setSupportRoles")) {
            return;
        }

        if (cmd.equalsIgnoreCase("setCreationMessage")) {
            return;
        }

        MessageSender.sendSimpleError("Invalid Argument", receivedEvent.getChannel());
        sendCommandUsage(receivedEvent.getChannel());
    }

}
