package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.queries.Tickets;
import de.eldoria.shepard.database.types.TicketType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.Replacer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.eldoria.shepard.database.queries.Tickets.getChannelOwnerRoles;
import static de.eldoria.shepard.database.queries.Tickets.removeChannel;
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
            openTicket(args, receivedEvent);
            return;
        }

        if (cmd.equalsIgnoreCase("close")) {
            if (args.length != 1) {
                MessageSender.sendSimpleError("Invalid Argument", receivedEvent.getChannel());
                return;
            }


            TextChannel channel = receivedEvent.getGuild().getTextChannelById(receivedEvent.getChannel().getIdLong());
            if (channel == null) {
                MessageSender.sendSimpleError("This is not a guild text channel!", receivedEvent.getChannel());
                return;
            }

            String channelOwnerId = Tickets.getChannelOwnerId(receivedEvent.getGuild(), channel, receivedEvent);

            if (channelOwnerId == null) {
                MessageSender.sendSimpleError("This is not a ticket channel!", receivedEvent.getChannel());
                return;
            }

            removeChannel(receivedEvent.getGuild(), channel, receivedEvent);

            List<String> ownerRolesAsString = getChannelOwnerRoles(receivedEvent.getGuild(), channel, receivedEvent);
            List<Role> ownerRoles = new ArrayList<>();

            for (String s : ownerRolesAsString) {
                Role roleById = receivedEvent.getGuild().getRoleById(s);
                if (roleById != null) {
                    ownerRoles.add(roleById);
                }
            }

            Member member = receivedEvent.getGuild().getMemberById(channelOwnerId);

            //If Member is present remove roles for this ticket.
            if (member != null) {
                List<String> channelIdsByOwner = Tickets.getChannelIdsByOwner(receivedEvent.getGuild(), member.getUser(), receivedEvent);

                List<TextChannel> channels = new ArrayList<>();

                for (String s : channelIdsByOwner) {
                    TextChannel textChannel = receivedEvent.getGuild().getTextChannelById(s);
                    if (textChannel != null) {
                        channels.add(textChannel);
                    }
                }

                Set<Role> roles = new HashSet<>();
                for (TextChannel c : channels) {
                    for (String s : getChannelOwnerRoles(receivedEvent.getGuild(), c, receivedEvent)) {
                        Role role = receivedEvent.getGuild().getRoleById(s);
                        if (role != null) {
                            roles.add(role);
                        }
                    }
                }

                for (Role r : ownerRoles) {
                    receivedEvent.getGuild().removeRoleFromMember(member, r).queue();
                }
                for (Role r : roles) {
                    receivedEvent.getGuild().addRoleToMember(member, r).queue();
                }
            }

            channel.delete().queue();
            return;
        }

        if (cmd.equalsIgnoreCase("showTypes")) {
            typeInfo(args, receivedEvent);
            return;
        }
        MessageSender.sendSimpleError("Invalid Argument", receivedEvent.getChannel());
        sendCommandUsage(receivedEvent.getChannel());
    }

    private void typeInfo(String[] args, MessageReceivedEvent receivedEvent) {
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
        } else if (args.length == 2) {
            List<MessageEmbed.Field> fields = new ArrayList<>();
            TicketType type = Tickets.getTypeByKeyword(receivedEvent.getGuild(), args[1], receivedEvent);

            if (type == null) {
                MessageSender.sendSimpleError("No such ticket type", receivedEvent.getChannel());
                return;
            }

            List<String> ownerMentions = new ArrayList<>();
            List<String> supporterMentions = new ArrayList<>();

            getValidRoles(receivedEvent.getGuild(),
                    Tickets.getTypeOwnerRoles(receivedEvent.getGuild(), type.getKeyword(), receivedEvent)
                            .toArray(String[]::new)).forEach(role -> ownerMentions.add(role.getAsMention()));

            getValidRoles(receivedEvent.getGuild(),
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
        }
    }

    private void openTicket(String[] args, MessageReceivedEvent receivedEvent) {
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

        String channelName = "#" + Tickets.getNextTicketCount(receivedEvent.getGuild(), receivedEvent)
                + " " + member.getUser().getName();

        TextChannel channel = receivedEvent.getGuild()
                .createTextChannel(channelName)
                .setParent(ticket.getCategory()).complete();

        Role everyone = receivedEvent.getGuild().getPublicRole();
        ChannelManager manager = channel.getManager().getChannel().getManager();

        PermissionOverrideAction everyoneOverride = manager.getChannel().createPermissionOverride(everyone);
        everyoneOverride.setDeny(Permission.MESSAGE_READ).queue();

        PermissionOverrideAction memberOverride = manager.getChannel().createPermissionOverride(member);
        memberOverride.setAllow(Permission.MESSAGE_READ).queue();

        Tickets.createChannel(receivedEvent.getGuild(), channel,
                member.getUser(), ticket.getKeyword(), receivedEvent);

        List<Role> supportRoles = getValidRoles(receivedEvent.getGuild(),
                Tickets.getTypeSupportRoles(receivedEvent.getGuild(), ticket.getKeyword(), receivedEvent));
        List<Role> ownerRoles = getValidRoles(receivedEvent.getGuild(),
                Tickets.getTypeOwnerRoles(receivedEvent.getGuild(), ticket.getKeyword(), receivedEvent));

        for (Role role : ownerRoles) {
            receivedEvent.getGuild().addRoleToMember(member, role).queue();
        }

        for (Role role : supportRoles) {
            PermissionOverrideAction override = manager.getChannel().createPermissionOverride(role);
            override.setAllow(Permission.MESSAGE_READ).queue();
        }

        MessageSender.sendMessage(Replacer.applyUserPlaceholder(member.getUser(), ticket.getCreationMessage()),
                channel);

        return;
    }
}
