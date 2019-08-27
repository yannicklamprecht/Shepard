package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.queries.Tickets;
import de.eldoria.shepard.database.types.TicketType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.Replacer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.internal.entities.RoleImpl;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
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
                                + "**showTypes** -> Leave empty or type keyword for type",
                        false)};
    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String cmd = args[0];
        if (cmd.equalsIgnoreCase("open")) {
            if (args.length != 3) {
                MessageSender.sendSimpleError("Invalid Argument", receivedEvent.getChannel());
            }
            Member member = receivedEvent.getGuild().getMemberById(DbUtil.getIdRaw(args[2]));
            if (member == null) {
                MessageSender.sendSimpleError("User not found!", receivedEvent.getChannel());
                return;
            }
            if (member.getId() == receivedEvent.getAuthor().getId()) {
                MessageSender.sendSimpleError("You can't open a ticket for yourself!", receivedEvent.getChannel());
                return;
            }
            TicketType ticket = Tickets.getTypeByKeyword(receivedEvent.getGuild(), args[1], receivedEvent);

            if (ticket == null) {
                MessageSender.sendSimpleError("Ticket type does not exists!", receivedEvent.getChannel());
                return;
            }

            String channelname = "#" + Tickets.getNextTicketCount(receivedEvent.getGuild(), receivedEvent)
                    + " " + member.getUser().getName();

            TextChannel channel = receivedEvent.getGuild()
                    .createTextChannel(channelname)
                    .setParent(ticket.getCategory()).complete();

            ChannelManager manager = channel.getManager().getChannel().getManager();
            receivedEvent.getGuild().getPublicRole().
            manager.getChannel().createPermissionOverride()

            Tickets.createChannel(receivedEvent.getGuild(), channel,
                    member.getUser(), ticket.getKeyword(), receivedEvent);


            MessageSender.sendMessage(Replacer.applyUserPlaceholder(member.getUser(), ticket.getCreationMessage()),
                    receivedEvent.getChannel());

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
            if (args.length == 1) {


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
                        .append("```yaml").append(lineSeparator())
                        .append(StringUtils.rightPad(typeKeyword, typeKeywordSize, " "))
                        .append(categoryName).append(lineSeparator());
                for (TicketType type : tickets) {
                    builder.append(StringUtils.rightPad(type.getKeyword() + ":", typeKeywordSize, " "))
                            .append(type.getCategory().getName()).append(lineSeparator());
                }
                builder.append("```");

                MessageSender.sendMessage(builder.toString(), receivedEvent.getChannel());
                return;
            } else if (args.length == 2) {
                List<MessageEmbed.Field> fields = new ArrayList<>();

                TicketType type = Tickets.getTypeByKeyword(receivedEvent.getGuild(), args[1], receivedEvent);

                if (type == null) {
                    MessageSender.sendSimpleError("No such ticket type", receivedEvent.getChannel());
                    return;
                }

                List<String> ownerMentions = new ArrayList<>();
                List<String> supporterMentions = new ArrayList<>();

                DbUtil.getValidRoles(receivedEvent.getGuild(),
                        Tickets.getTypeOwnerRoles(receivedEvent.getGuild(), type.getKeyword(), receivedEvent)
                                .toArray(String[]::new)).forEach(role -> ownerMentions.add(role.getAsMention()));

                DbUtil.getValidRoles(receivedEvent.getGuild(),
                        Tickets.getTypeSupportRoles(receivedEvent.getGuild(), type.getKeyword(), receivedEvent)
                                .toArray(String[]::new)).forEach(role -> supporterMentions.add(role.getAsMention()));

                fields.add(new MessageEmbed.Field("Channel Category:", type.getCategory().getName(), false));
                fields.add(new MessageEmbed.Field("Creation Message:", type.getCreationMessage(), false));
                fields.add(new MessageEmbed.Field("Ticket Owner Groups:",
                        String.join(lineSeparator() + "", ownerMentions), false));
                fields.add(new MessageEmbed.Field("Ticket Supporter Groups:",
                        String.join(lineSeparator() + "", supporterMentions), false));

                MessageSender.sendTextBox("Information about Ticket Type: \"" + type.getKeyword() + "\"",
                        fields, receivedEvent.getChannel());
                return;
            }
        }
        MessageSender.sendSimpleError("Invalid Argument", receivedEvent.getChannel());
        sendCommandUsage(receivedEvent.getChannel());
    }
}
