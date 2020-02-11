package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArgument;
import de.eldoria.shepard.contexts.commands.argument.SubArgument;
import de.eldoria.shepard.database.queries.TicketData;
import de.eldoria.shepard.database.types.TicketType;
import de.eldoria.shepard.localization.enums.WordsLocale;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.Replacer;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.database.queries.TicketData.getTypeOwnerRoles;
import static de.eldoria.shepard.database.queries.TicketData.getTypeSupportRoles;
import static de.eldoria.shepard.database.queries.TicketData.removeChannel;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketLocale.A_CLOSE;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketLocale.A_INFO;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketLocale.A_TICKET_TYPE;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketLocale.C_CLOSE;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketLocale.C_INFO;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketLocale.C_OPEN;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketLocale.M_CHANNEL_CATEGORY;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketLocale.M_CREATION_MESSAGE;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketLocale.M_OPEN;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketLocale.M_TICKET_OWNER_ROLES;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketLocale.M_TICKET_SUPPORT_ROLES;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketLocale.M_TYPE_ABOUT;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketLocale.M_TYPE_LIST;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static java.lang.System.lineSeparator;

/**
 * Command to manage tickets.
 * Allows open, close of tickets and ticket category info.
 */
public class Ticket extends Command {

    /**
     * Create ticket command object.
     */
    public Ticket() {
        commandName = "ticket";
        commandAliases = new String[] {"t"};
        commandDesc = DESCRIPTION.tag;
        commandArguments = new CommandArgument[] {
                new CommandArgument("action", true,
                        new SubArgument("open", C_OPEN.tag, true),
                        new SubArgument("close", C_CLOSE.tag, true),
                        new SubArgument("info", C_INFO.tag, true)),
                new CommandArgument("value", false,
                        new SubArgument("open", A_TICKET_TYPE + " " + GeneralLocale.A_USER),
                        new SubArgument("close", A_CLOSE.tag),
                        new SubArgument("info", A_INFO.tag))
        };
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        CommandArgument arg = commandArguments[0];

        if (arg.isSubCommand(cmd, 0)) {
            open(args, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 1)) {
            close(args, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 2)) {
            info(args, messageContext);
            return;
        }
        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
    }

    private void close(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 1) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }


        TextChannel channel = messageContext.getTextChannel();
        String channelOwnerId = TicketData.getChannelOwnerId(messageContext.getGuild(), channel, messageContext);

        if (channelOwnerId == null) {
            MessageSender.sendSimpleError(ErrorType.NOT_TICKET_CHANNEL, messageContext.getTextChannel());
            return;
        }

        //Get the ticket type for caching.
        TicketType type = TicketData.getTypeByChannel(messageContext.getGuild(), channel, messageContext);

        //Removes channel from database. needed for further role checking.
        if (removeChannel(messageContext.getGuild(), channel, messageContext)) {


            //Get the ticket owner member object
            Member member = ArgumentParser.getGuildMember(messageContext.getGuild(), channelOwnerId);

            //If Member is present remove roles for this ticket.
            if (member != null && type != null) {
                //Get the owner roles of the current ticket. They should be removed.
                List<Role> roles = ArgumentParser.getRoles(messageContext.getGuild(),
                        getTypeOwnerRoles(messageContext.getGuild(), type.getKeyword(), messageContext));
                TicketHelper.removeAndUpdateTicketRoles(messageContext, member, roles);
            }

            //Finally delete the channel.
            channel.delete().queue();
        }
    }


    private void info(String[] args, MessageEventDataWrapper messageContext) {
        List<TicketType> tickets = TicketData.getTypes(messageContext.getGuild(), messageContext);
        if (tickets.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.NO_TICKET_TYPES_DEFINED, messageContext.getTextChannel());
            return;
        }

        //Return list fo available ticket types
        if (args.length == 1) {

            TextFormatting.TableBuilder tableBuilder = TextFormatting.getTableBuilder(
                    tickets, WordsLocale.KEYWORD.tag, "", WordsLocale.CATEGORY.tag);

            for (TicketType type : tickets) {
                tableBuilder.next();
                tableBuilder.setRow(type.getKeyword(), ":", type.getCategory().getName());
            }

            MessageSender.sendMessage("**__" + M_TYPE_LIST + "__**" + lineSeparator()
                    + tableBuilder, messageContext.getTextChannel());
        } else if (args.length == 2) {
            //Return info for one ticket type.
            TicketType type = TicketData.getTypeByKeyword(messageContext.getGuild(), args[1], messageContext);

            if (type == null) {
                MessageSender.sendSimpleError(ErrorType.TYPE_NOT_FOUND, messageContext.getTextChannel());
                return;
            }

            List<String> ownerMentions = ArgumentParser.getRoles(messageContext.getGuild(),
                    getTypeOwnerRoles(messageContext.getGuild(), type.getKeyword(), messageContext))
                    .stream().map(IMentionable::getAsMention).collect(Collectors.toList());

            List<String> supporterMentions = ArgumentParser.getRoles(messageContext.getGuild(),
                    getTypeSupportRoles(messageContext.getGuild(), type.getKeyword(), messageContext))
                    .stream().map(IMentionable::getAsMention).collect(Collectors.toList());

            List<LocalizedField> fields = new ArrayList<>();
            fields.add(new LocalizedField(M_CHANNEL_CATEGORY.tag, type.getCategory().getName(), false,
                    messageContext));
            fields.add(new LocalizedField(M_CREATION_MESSAGE.tag, type.getCreationMessage(), false,
                    messageContext));
            fields.add(new LocalizedField(M_TICKET_OWNER_ROLES.tag,
                    String.join(lineSeparator() + "", ownerMentions), false, messageContext));
            fields.add(new LocalizedField(M_TICKET_SUPPORT_ROLES.tag,
                    String.join(lineSeparator() + "", supporterMentions), false, messageContext));

            MessageSender.sendTextBox(M_TYPE_ABOUT + " **" + type.getKeyword() + "**",
                    fields, messageContext.getTextChannel());
        }
    }

    private void open(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }

        Member member = ArgumentParser.getGuildMember(messageContext.getGuild(), args[2]);
        if (member == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, messageContext.getTextChannel());
            return;
        }

        if (Verifier.equalSnowflake(member, messageContext.getAuthor())) {
            MessageSender.sendSimpleError(ErrorType.TICKET_SELF_ASSIGNMENT, messageContext.getTextChannel());
            return;
        }

        TicketType ticket = TicketData.getTypeByKeyword(messageContext.getGuild(), args[1], messageContext);

        if (ticket == null) {
            MessageSender.sendSimpleError(ErrorType.TYPE_NOT_FOUND, messageContext.getTextChannel());
            return;
        }

        //Set Channel Name
        String channelName = TicketData.getNextTicketCount(messageContext.getGuild(), messageContext)
                + " " + member.getUser().getName();

        //Create channel and wait for creation
        messageContext.getGuild()
                .createTextChannel(channelName)
                .setParent(ticket.getCategory())
                .queue(channel -> {
                    //Manage permissions for @everyone and deny read permission
                    Role everyone = messageContext.getGuild().getPublicRole();
                    ChannelManager manager = channel.getManager().getChannel().getManager();

                    PermissionOverrideAction everyoneOverride = manager.getChannel().createPermissionOverride(everyone);
                    everyoneOverride.setDeny(Permission.MESSAGE_READ).queue();

                    PermissionOverrideAction memberOverride;
                    try {
                        //Gives ticket owner read permission in channel
                        memberOverride = manager.getChannel().createPermissionOverride(member);
                    } catch (IllegalStateException e) {
                        memberOverride = manager.getChannel().putPermissionOverride(member);
                    }
                    memberOverride.setAllow(Permission.MESSAGE_READ).queue();

                    //Saves channel in database

                    if (!TicketData.createChannel(messageContext.getGuild(), channel,
                            member.getUser(), ticket.getKeyword(), messageContext)) {
                        channel.delete().queue();
                        return;
                    }

                    //Get ticket support and owner roles
                    List<Role> supportRoles = ArgumentParser.getRoles(messageContext.getGuild(),
                            getTypeSupportRoles(messageContext.getGuild(), ticket.getKeyword(), messageContext));

                    List<Role> ownerRoles = ArgumentParser.getRoles(messageContext.getGuild(),
                            getTypeOwnerRoles(messageContext.getGuild(), ticket.getKeyword(), messageContext));
                    //Assign ticket support and owner roles
                    for (Role role : ownerRoles) {
                        messageContext.getGuild().addRoleToMember(member, role).queue();
                    }

                    for (Role role : supportRoles) {
                        PermissionOverrideAction override = manager.getChannel().createPermissionOverride(role);
                        override.setAllow(Permission.MESSAGE_READ).queue();
                    }

                    //Greet ticket owner in ticket channel
                    MessageSender.sendMessageToChannel(
                            Replacer.applyUserPlaceholder(member.getUser(), ticket.getCreationMessage()),
                            channel);

                    MessageSender.sendMessage(localizeAllAndReplace(M_OPEN.tag, messageContext.getGuild(),
                            channel.getAsMention(), member.getAsMention()), messageContext.getTextChannel());
                });
    }
}
