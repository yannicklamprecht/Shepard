package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.queries.Tickets;
import de.eldoria.shepard.database.types.TicketType;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.eldoria.shepard.database.DbUtil.getValidRoles;
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
                                + "**setChannelCategory** -> Sets Channel Category for support channel." + lineSeparator()
                                + "**setCreationMessage** -> Sets the creation Message of the ticket type" + lineSeparator()
                                + "Message will be send when a ticket is created.",
                        true),
                new CommandArg("value",
                        "**createType** -> [type_name] [channel_category_id]" + lineSeparator()
                                + "**removeType** -> [type_name]" + lineSeparator()
                                + "**setOwnerRoles** -> [type_name] [Roles...] One or more Roles" + lineSeparator()
                                + "**setSupportRoles** -> [type_name] [Roles...] One or more Roles" + lineSeparator()
                                + "**setChannelCategory** -> [type_name] [channel_category_id]" + lineSeparator()
                                + "**setCreation Message** -> [type_names] [Message]" + lineSeparator()
                                + "Supported Placeholder: {user_name} {user_tag} {user_mention}",
                        true)};

    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String cmd = args[0];
        String type = args[1];
        List<TicketType> tickets = Tickets.getTypes(receivedEvent.getGuild(), receivedEvent);
        TicketType scopeTicket = null;
        for (TicketType ticket : tickets) {
            if (ticket.getKeyword().equalsIgnoreCase(type)) {
                scopeTicket = ticket;
                break;
            }
        }

        if (cmd.equalsIgnoreCase("createType")) {
            createType(args, receivedEvent, type, scopeTicket);
            return;
        }

        if (scopeTicket == null) {
            MessageSender.sendSimpleError("Ticket type not found.", receivedEvent.getChannel());
            return;
        }

        if (cmd.equalsIgnoreCase("removeType")) {
            removeType(args, receivedEvent, scopeTicket);
            return;
        }

        if (cmd.equalsIgnoreCase("setOwnerRoles")
                || cmd.equalsIgnoreCase("setSupportRoles")) {
            setRoles(args, receivedEvent, cmd, scopeTicket);
            return;
        }

        if (cmd.equalsIgnoreCase("setChannelCategory")) {
            setChannelCategory(args, receivedEvent, scopeTicket);
            return;
        }

        if (cmd.equalsIgnoreCase("setCreationMessage")) {
            setCreationMessage(args, receivedEvent, scopeTicket);
            return;
        }

        MessageSender.sendSimpleError("Invalid Argument", receivedEvent.getChannel());
        sendCommandUsage(receivedEvent.getChannel());
    }

    private void setCreationMessage(String[] args, MessageReceivedEvent receivedEvent, TicketType scopeTicket) {
        if (args.length < 3) {
            MessageSender.sendSimpleError("Invalid arguments", receivedEvent.getChannel());
            sendCommandUsage(receivedEvent.getChannel());
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        Tickets.addType(receivedEvent.getGuild(), null, message,
                scopeTicket.getKeyword(), receivedEvent);
        MessageSender.sendSimpleTextBox("Set creation text for ticket type " + scopeTicket.getKeyword() + " to:",
                message, receivedEvent.getChannel());

    }

    private void setChannelCategory(String[] args, MessageReceivedEvent receivedEvent, TicketType scopeTicket) {
        if (args.length != 3) {
            MessageSender.sendSimpleError("Invalid arguments", receivedEvent.getChannel());
            sendCommandUsage(receivedEvent.getChannel());
            return;
        }

        Category category = receivedEvent.getGuild().getCategoryById(args[2]);

        if (category == null) {
            MessageSender.sendSimpleError("Unknown Category", receivedEvent.getChannel());
            return;
        }

        Tickets.addType(receivedEvent.getGuild(), category, null,
                scopeTicket.getKeyword(), receivedEvent);
        MessageSender.sendMessage("Set channel category for ticket type \"" + scopeTicket.getKeyword()
                + "\" to " + category.getName(), receivedEvent.getChannel());
    }

    private void setRoles(String[] args, MessageReceivedEvent receivedEvent, String cmd, TicketType scopeTicket) {
        if (args.length < 3) {
            MessageSender.sendSimpleError("Invalid arguments", receivedEvent.getChannel());
            sendCommandUsage(receivedEvent.getChannel());
            return;
        }
        String[] roleIds = Arrays.copyOfRange(args, 2, args.length);
        List<Role> validRoles = getValidRoles(receivedEvent.getGuild(), roleIds);

        List<String> roleMentions = new ArrayList<>();
        validRoles.forEach(role -> roleMentions.add(role.getAsMention()));

        if (cmd.equalsIgnoreCase("setOwnerRoles")) {
            Tickets.setTypeOwnerRoles(receivedEvent.getGuild(), scopeTicket.getKeyword(), validRoles, receivedEvent);

            MessageSender.sendSimpleTextBox("Set the following roles as owner roles for ticket "
                            + scopeTicket.getKeyword(), String.join(lineSeparator() + "", roleMentions),
                    receivedEvent.getChannel());
        } else {
            Tickets.setTypeSupportRoles(receivedEvent.getGuild(), scopeTicket.getKeyword(), validRoles, receivedEvent);

            MessageSender.sendSimpleTextBox("Set the following roles as owner roles for ticket "
                            + scopeTicket.getKeyword(), String.join(lineSeparator() + "", roleMentions),
                    receivedEvent.getChannel());
        }
    }

    private void removeType(String[] args, MessageReceivedEvent receivedEvent, TicketType scopeTicket) {
        if (args.length != 2) {
            MessageSender.sendSimpleError("Invalid arguments", receivedEvent.getChannel());
            sendCommandUsage(receivedEvent.getChannel());
            return;
        }

        Tickets.removeTypeByKeyword(receivedEvent.getGuild(), scopeTicket.getKeyword(), receivedEvent);

        MessageSender.sendMessage("Remove ticket type **" + scopeTicket.getKeyword() + "**!",
                receivedEvent.getChannel());
    }

    private void createType(String[] args, MessageReceivedEvent receivedEvent, String type, TicketType scopeTicket) {
        if (args.length != 3) {
            MessageSender.sendSimpleError("Invalid arguments", receivedEvent.getChannel());
            sendCommandUsage(receivedEvent.getChannel());
            return;
        }

        if (scopeTicket != null) {
            MessageSender.sendSimpleError("This type is already defined.", receivedEvent.getChannel());
            return;
        }

        Category category = receivedEvent.getGuild().getCategoryById(args[2]);

        if (category == null) {
            MessageSender.sendSimpleError("No category found with this id.", receivedEvent.getChannel());
            return;
        }

        Tickets.addType(receivedEvent.getGuild(), category, "", type, receivedEvent);

        MessageSender.sendMessage("Created ticket type: **" + type.toLowerCase() + "**", receivedEvent.getChannel());

        return;
    }


}
