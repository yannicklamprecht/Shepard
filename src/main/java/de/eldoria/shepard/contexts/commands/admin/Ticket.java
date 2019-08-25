package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.queries.Tickets;
import de.eldoria.shepard.database.types.TicketType;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang.StringUtils;

import java.util.List;

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
            List<TicketType> tickets = Tickets.getTypes(receivedEvent.getGuild(), receivedEvent);
            if (tickets.size() == 0) {
                MessageSender.sendMessage("No ticket types defined", receivedEvent.getChannel());
                return;
            }

            String categoryName = "Category Name";
            int categoryNameSize = categoryName.length();
            String typeKeyword = "Type/Keyword ";
            int typeKeywordSize = typeKeyword.length();

            for (TicketType type : tickets) {
                if (type.getCategory() == null) {
                    continue;
                }
                int categoryLength = type.getCategory().getName().length();
                if (categoryLength > categoryNameSize) {
                    categoryNameSize = categoryLength;
                }

                if (type.getKeyword().length() + 1 > typeKeywordSize) {
                    typeKeywordSize = type.getKeyword().length();
                }
            }

            StringBuilder builder = new StringBuilder();
            builder.append("**__Ticket Types with Categories:__**").append(lineSeparator())
                    .append("```yaml")
                    .append(StringUtils.rightPad(typeKeyword, typeKeywordSize, " "))
                    .append(categoryName).append(lineSeparator());
            for (TicketType type : tickets) {
                builder.append(StringUtils.rightPad(type.getKeyword(), typeKeywordSize, " "))
                        .append(type.getCategory().getName()).append(lineSeparator());
            }
            builder.append("```");

            MessageSender.sendMessage(builder.toString(), receivedEvent.getChannel());
            return;
        }
        MessageSender.sendSimpleError("Invalid Argument", receivedEvent.getChannel());
        sendCommandUsage(receivedEvent.getChannel());
    }
}
