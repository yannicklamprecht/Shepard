package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.queries.TicketData;
import de.eldoria.shepard.database.types.TicketType;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.Replacer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static de.eldoria.shepard.database.queries.TicketData.getTypeOwnerRoles;
import static de.eldoria.shepard.database.queries.TicketData.getTypeSupportRoles;
import static de.eldoria.shepard.database.queries.TicketData.removeChannel;
import static de.eldoria.shepard.util.Verifier.getValidRoles;
import static java.lang.System.lineSeparator;

public class Ticket extends Command {

    /**
     * Create ticket command object.
     */
    public Ticket() {
        commandName = "ticket";
        commandAliases = new String[] {"t"};
        commandDesc = "Ticket system for creation of channels to help users";
        commandArgs = new CommandArg[] {
                new CommandArg("action",
                        "**__o__pen** -> Open a new ticket" + lineSeparator()
                                + "**__c__lose** -> Close a ticket" + lineSeparator()
                                + "**__l__ist** -> Shows a list of all available ticket types",
                        true),
                new CommandArg("value",
                        "**open** -> [ticket_type] [user_name]" + lineSeparator()
                                + "**close** -> Leave empty. Execute in channel which you want to close."
                                + lineSeparator()
                                + "**list** -> Leave empty for a overview or type keyword for further type infos",
                        false)};
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        if (cmd.equalsIgnoreCase("open") || cmd.equalsIgnoreCase("o")) {
            openTicket(args, messageContext);
            return;
        }

        if (cmd.equalsIgnoreCase("close") || cmd.equalsIgnoreCase("c")) {
            close(args, messageContext);
            return;
        }

        if (cmd.equalsIgnoreCase("list") || cmd.equalsIgnoreCase("l")) {
            typeInfo(args, messageContext);
            return;
        }
        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getChannel());
        sendCommandUsage(messageContext.getChannel());
    }

    private void close(String[] args, MessageEventDataWrapper receivedEvent) {
        if (args.length != 1) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, receivedEvent.getChannel());
            return;
        }


        TextChannel channel = receivedEvent.getGuild().getTextChannelById(receivedEvent.getChannel().getIdLong());
        if (channel == null) {
            MessageSender.sendSimpleError(ErrorType.NOT_GUILD_TEXT_CHANNEL, receivedEvent.getChannel());
            return;
        }

        String channelOwnerId = TicketData.getChannelOwnerId(receivedEvent.getGuild(), channel, receivedEvent);

        if (channelOwnerId == null) {
            MessageSender.sendSimpleError(ErrorType.NOT_TICKET_CHANEL, receivedEvent.getChannel());
            return;
        }

        //Get the ticket type for caching.
        TicketType type = TicketData.getTypeByChannel(receivedEvent.getGuild(), channel, receivedEvent);

        //Removes channel from database. needed for further role checking.
        if (removeChannel(receivedEvent.getGuild(), channel, receivedEvent)) {


            //Get the ticket owner member object
            Member member = receivedEvent.getGuild().getMemberById(channelOwnerId);

            //If Member is present remove roles for this ticket.
            if (member != null) {
                //Get the owner roles of the current ticket. They should be removed.
                if (type != null) {
                    List<String> ownerRolesAsString = getTypeOwnerRoles(receivedEvent.getGuild(),
                            type.getKeyword(), receivedEvent);
                    TicketHelper.removeAndUpdateTicketRoles(receivedEvent, member, ownerRolesAsString);
                }
            }

            //Finally delete the channel.
            channel.delete().queue();
        }
    }


    private void typeInfo(String[] args, MessageEventDataWrapper receivedEvent) {
        List<TicketType> tickets = TicketData.getTypes(receivedEvent.getGuild(), receivedEvent);
        if (tickets.size() == 0) {
            MessageSender.sendMessage("No ticket types defined", receivedEvent.getChannel());
            return;
        }
        //Return list fo available ticket types
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
        } else if (args.length == 2) {
            //Return info for one ticket type.
            TicketType type = TicketData.getTypeByKeyword(receivedEvent.getGuild(), args[1], receivedEvent);

            if (type == null) {
                MessageSender.sendSimpleError(ErrorType.TYPE_NOT_FOUND, receivedEvent.getChannel());
                return;
            }

            List<String> ownerMentions = new ArrayList<>();

            getValidRoles(receivedEvent.getGuild(),
                    getTypeOwnerRoles(receivedEvent.getGuild(), type.getKeyword(), receivedEvent)
                            .toArray(String[]::new)).forEach(role -> ownerMentions.add(role.getAsMention()));

            List<String> supporterMentions = new ArrayList<>();
            getValidRoles(receivedEvent.getGuild(),
                    getTypeSupportRoles(receivedEvent.getGuild(), type.getKeyword(), receivedEvent)
                            .toArray(String[]::new)).forEach(role -> supporterMentions.add(role.getAsMention()));

            List<MessageEmbed.Field> fields = new ArrayList<>();
            fields.add(new MessageEmbed.Field("Channel Category:", type.getCategory().getName(), false));
            fields.add(new MessageEmbed.Field("Creation Message:", type.getCreationMessage(), false));
            fields.add(new MessageEmbed.Field("Ticket Owner Groups:",
                    String.join(lineSeparator() + "", ownerMentions), false));
            fields.add(new MessageEmbed.Field("Ticket Supporter Groups:",
                    String.join(lineSeparator() + "", supporterMentions), false));

            MessageSender.sendTextBox("Information about Ticket Type: \"" + type.getKeyword() + "\"",
                    fields, receivedEvent.getChannel());
        }
    }

    private void openTicket(String[] args, MessageEventDataWrapper receivedEvent) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, receivedEvent.getChannel());
        }
        Member member = receivedEvent.getGuild().getMemberById(DbUtil.getIdRaw(args[2]));
        if (member == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, receivedEvent.getChannel());
            return;
        }
        if (member.getIdLong() == receivedEvent.getAuthor().getIdLong()) {
            MessageSender.sendSimpleError(ErrorType.TICKET_SELF_ASSIGNMENT, receivedEvent.getChannel());
            return;
        }

        TicketType ticket = TicketData.getTypeByKeyword(receivedEvent.getGuild(), args[1], receivedEvent);

        if (ticket == null) {
            MessageSender.sendSimpleError(ErrorType.TYPE_NOT_FOUND, receivedEvent.getChannel());
            return;
        }

        //Set Channel Name
        String channelName = TicketData.getNextTicketCount(receivedEvent.getGuild(), receivedEvent)
                + " " + member.getUser().getName();

        //Create channel and wait for creation
        TextChannel channel = receivedEvent.getGuild()
                .createTextChannel(channelName)
                .setParent(ticket.getCategory()).complete();

        //Manage permissions for @everyone and deny read permission
        Role everyone = receivedEvent.getGuild().getPublicRole();
        ChannelManager manager = channel.getManager().getChannel().getManager();

        PermissionOverrideAction everyoneOverride = manager.getChannel().createPermissionOverride(everyone);
        everyoneOverride.setDeny(Permission.MESSAGE_READ).queue();

        //Gives ticket owner read permission in channel
        PermissionOverrideAction memberOverride = manager.getChannel().createPermissionOverride(member);
        memberOverride.setAllow(Permission.MESSAGE_READ).queue();

        //Saves channel in database

        TicketData.createChannel(receivedEvent.getGuild(), channel,
                member.getUser(), ticket.getKeyword(), receivedEvent);

        //Get ticket support and owner roles
        List<Role> supportRoles = getValidRoles(receivedEvent.getGuild(),
                getTypeSupportRoles(receivedEvent.getGuild(), ticket.getKeyword(), receivedEvent));

        List<Role> ownerRoles = getValidRoles(receivedEvent.getGuild(),
                getTypeOwnerRoles(receivedEvent.getGuild(), ticket.getKeyword(), receivedEvent));
        //Assign ticket support and owner roles
        for (Role role : ownerRoles) {
            receivedEvent.getGuild().addRoleToMember(member, role).queue();
        }

        for (Role role : supportRoles) {
            PermissionOverrideAction override = manager.getChannel().createPermissionOverride(role);
            override.setAllow(Permission.MESSAGE_READ).queue();
        }

        //Greet ticket owner in ticket channel
        MessageSender.sendMessage(Replacer.applyUserPlaceholder(member.getUser(), ticket.getCreationMessage()),
                channel);
    }
}
