package de.eldoria.shepard.commandmodules.ticketsystem.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.ticketsystem.data.TicketData;
import de.eldoria.shepard.commandmodules.ticketsystem.util.TicketHelper;
import de.eldoria.shepard.commandmodules.ticketsystem.util.TicketType;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.WordsLocale;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.enums.commands.admin.TicketLocale;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.Replacer;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.ChannelManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.admin.TicketLocale.AD_TICKET_TYPE;
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
public class Ticket extends Command implements Executable, ReqParser, ReqDataSource, ReqInit {

    private ArgumentParser parser;
    private DataSource source;
    private TicketData ticketData;

    /**
     * Create ticket command object.
     */
    public Ticket() {
        super("ticket",
                new String[] {"t"},
                DESCRIPTION.tag,
                SubCommand.builder("ticket")
                        .addSubcommand(C_OPEN.tag,
                                Parameter.createCommand("open"),
                                Parameter.createInput(A_TICKET_TYPE.tag, AD_TICKET_TYPE.tag, true),
                                Parameter.createInput(GeneralLocale.A_USER.tag, GeneralLocale.AD_USER.tag, true))
                        .addSubcommand(C_CLOSE.tag,
                                Parameter.createCommand("close"))
                        .addSubcommand(C_INFO.tag,
                                Parameter.createCommand("info"),
                                Parameter.createInput(A_TICKET_TYPE.tag, A_INFO.tag, false))
                        .build(),
                CommandCategory.ADMIN);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        SubCommand arg = subCommands[0];

        if (isSubCommand(cmd, 0)) {
            open(args, messageContext);
            return;
        }

        if (isSubCommand(cmd, 1)) {
            close(args, messageContext);
            return;
        }

        if (isSubCommand(cmd, 2)) {
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
        String channelOwnerId = ticketData.getChannelOwnerId(messageContext.getGuild(), channel, messageContext);

        if (channelOwnerId == null) {
            MessageSender.sendSimpleError(ErrorType.NOT_TICKET_CHANNEL, messageContext.getTextChannel());
            return;
        }

        //Get the ticket type for caching.
        TicketType type = ticketData.getTypeByChannel(messageContext.getGuild(), channel, messageContext);

        //Removes channel from database. needed for further role checking.
        if (ticketData.removeChannel(messageContext.getGuild(), channel, messageContext)) {


            //Get the ticket owner member object
            Member member = parser.getGuildMember(messageContext.getGuild(), channelOwnerId);

            //If Member is present remove roles for this ticket.
            if (member != null && type != null) {
                //Get the owner roles of the current ticket. They should be removed.
                List<Role> roles = parser.getRoles(messageContext.getGuild(),
                        ticketData.getTypeOwnerRoles(messageContext.getGuild(), type.getKeyword(), messageContext));
                TicketHelper.removeAndUpdateTicketRoles(ticketData, parser, messageContext, member, roles);
            }

            //Finally delete the channel.
            channel.delete().queue();
        }
    }


    private void info(String[] args, MessageEventDataWrapper messageContext) {
        List<TicketType> tickets = ticketData.getTypes(messageContext.getGuild(), messageContext);
        if (tickets.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.NO_TICKET_TYPES_DEFINED, messageContext.getTextChannel());
            return;
        }

        //Return list fo available ticket types
        if (args.length == 1) {

            TextFormatting.TableBuilder tableBuilder = TextFormatting.getTableBuilder(
                    tickets,
                    TextLocalizer.localizeAll(WordsLocale.KEYWORD.tag, messageContext.getGuild()), "",
                    TextLocalizer.localizeAll(WordsLocale.CATEGORY.tag, messageContext.getGuild()));

            for (TicketType type : tickets) {
                tableBuilder.next();
                Category category = type.getCategory();
                tableBuilder.setRow(type.getKeyword(), ":",
                        category != null ? category.getName()
                                : TextLocalizer.localizeAll(WordsLocale.INVALID.tag, messageContext.getGuild()));
            }

            MessageSender.sendMessage("**__" + M_TYPE_LIST + "__**" + lineSeparator()
                    + tableBuilder, messageContext.getTextChannel());
        } else if (args.length == 2) {
            //Return info for one ticket type.
            TicketType type = ticketData.getTypeByKeyword(messageContext.getGuild(), args[1], messageContext);

            if (type == null) {
                MessageSender.sendSimpleError(ErrorType.TYPE_NOT_FOUND, messageContext.getTextChannel());
                return;
            }

            List<String> ownerMentions = parser.getRoles(messageContext.getGuild(),
                    ticketData.getTypeOwnerRoles(messageContext.getGuild(), type.getKeyword(), messageContext))
                    .stream().map(IMentionable::getAsMention).collect(Collectors.toList());

            List<String> supporterMentions = parser.getRoles(messageContext.getGuild(),
                    ticketData.getTypeSupportRoles(messageContext.getGuild(), type.getKeyword(), messageContext))
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

        Member member = parser.getGuildMember(messageContext.getGuild(), args[2]);
        if (member == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, messageContext.getTextChannel());
            return;
        }

        if (Verifier.equalSnowflake(member, messageContext.getAuthor())) {
            MessageSender.sendSimpleError(ErrorType.TICKET_SELF_ASSIGNMENT, messageContext.getTextChannel());
            return;
        }

        TicketType ticket = ticketData.getTypeByKeyword(messageContext.getGuild(), args[1], messageContext);

        if (ticket == null) {
            MessageSender.sendSimpleError(ErrorType.TYPE_NOT_FOUND, messageContext.getTextChannel());
            return;
        }

        if (ticket.getCategory() == null) {
            MessageSender.sendMessage(TicketLocale.M_CATEGORY_NOT_FOUND.tag, messageContext.getTextChannel());
            return;
        }

        //Set Channel Name
        String channelName = ticketData.getNextTicketCount(messageContext.getGuild(), messageContext)
                + " " + member.getUser().getName();

        //Create channel and wait for creation
        messageContext.getGuild()
                .createTextChannel(channelName)
                .setParent(ticket.getCategory())
                .queue(channel -> {
                    //Manage permissions for @everyone and deny read permission
                    Role everyone = messageContext.getGuild().getPublicRole();
                    ChannelManager manager = channel.getManager().getChannel().getManager();

                    manager.getChannel()
                            .upsertPermissionOverride(everyone)
                            .setDeny(Permission.MESSAGE_READ).queue();

                    //Gives ticket owner read permission in channel
                    manager.getChannel()
                            .upsertPermissionOverride(member)
                            .setAllow(Permission.MESSAGE_READ).queue();

                    //Saves channel in database

                    if (!ticketData.createChannel(messageContext.getGuild(), channel,
                            member.getUser(), ticket.getKeyword(), messageContext)) {
                        channel.delete().queue();
                        return;
                    }

                    //Get ticket support and owner roles
                    List<Role> supportRoles = parser.getRoles(messageContext.getGuild(),
                            ticketData.getTypeSupportRoles(messageContext.getGuild(),
                                    ticket.getKeyword(), messageContext));

                    List<Role> ownerRoles = parser.getRoles(messageContext.getGuild(),
                            ticketData.getTypeOwnerRoles(messageContext.getGuild(),
                                    ticket.getKeyword(), messageContext));
                    //Assign ticket support and owner roles
                    for (Role role : ownerRoles) {
                        messageContext.getGuild().addRoleToMember(member, role).queue();
                    }

                    for (Role role : supportRoles) {
                        manager.getChannel().upsertPermissionOverride(role)
                                .setAllow(Permission.MESSAGE_READ).queue();
                    }

                    //Greet ticket owner in ticket channel
                    MessageSender.sendMessageToChannel(
                            Replacer.applyUserPlaceholder(member.getUser(), ticket.getCreationMessage()),
                            channel);

                    MessageSender.sendMessage(localizeAllAndReplace(M_OPEN.tag, messageContext.getGuild(),
                            channel.getAsMention(), "**" + member.getEffectiveName() + "**"),
                            messageContext.getTextChannel());
                });
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    @Override
    public void init() {
        ticketData = new TicketData(source);
    }
}
