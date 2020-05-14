package de.eldoria.shepard.commandmodules.ticketsystem.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.command.GuildChannelOnly;
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
import de.eldoria.shepard.wrapper.EventWrapper;
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
public class Ticket extends Command implements GuildChannelOnly, Executable, ReqParser, ReqDataSource, ReqInit {

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
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String cmd = args[0];

        if (isSubCommand(cmd, 0)) {
            open(args, wrapper);
            return;
        }

        if (isSubCommand(cmd, 1)) {
            close(args, wrapper);
            return;
        }

        if (isSubCommand(cmd, 2)) {
            info(args, wrapper);
            return;
        }
    }

    private void close(String[] args, EventWrapper wrapper) {
        if (args.length != 1) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, wrapper);
            return;
        }


        TextChannel channel = wrapper.getTextChannel().get();
        String channelOwnerId = ticketData.getChannelOwnerId(wrapper.getGuild().get(), channel, wrapper);

        if (channelOwnerId == null) {
            MessageSender.sendSimpleError(ErrorType.NOT_TICKET_CHANNEL, wrapper);
            return;
        }

        //Get the ticket type for caching.
        TicketType type = ticketData.getTypeByChannel(wrapper.getGuild().get(), channel, wrapper);

        //Removes channel from database. needed for further role checking.
        if (ticketData.removeChannel(wrapper.getGuild().get(), channel, wrapper)) {


            //Get the ticket owner member object
            Member member = parser.getGuildMember(wrapper.getGuild().get(), channelOwnerId);

            //If Member is present remove roles for this ticket.
            if (member != null && type != null) {
                //Get the owner roles of the current ticket. They should be removed.
                List<Role> roles = parser.getRoles(wrapper.getGuild().get(),
                        ticketData.getTypeOwnerRoles(wrapper.getGuild().get(), type.getKeyword(), wrapper));
                TicketHelper.removeAndUpdateTicketRoles(ticketData, parser, wrapper, member, roles);
            }

            //Finally delete the channel.
            channel.delete().queue();
        }
    }


    private void info(String[] args, EventWrapper wrapper) {
        List<TicketType> tickets = ticketData.getTypes(wrapper.getGuild().get(), wrapper);
        if (tickets.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.NO_TICKET_TYPES_DEFINED, wrapper);
            return;
        }

        //Return list fo available ticket types
        if (args.length == 1) {

            TextFormatting.TableBuilder tableBuilder = TextFormatting.getTableBuilder(
                    tickets,
                    TextLocalizer.localizeAll(WordsLocale.KEYWORD.tag, wrapper), "",
                    TextLocalizer.localizeAll(WordsLocale.CATEGORY.tag, wrapper));

            for (TicketType type : tickets) {
                tableBuilder.next();
                Category category = type.getCategory();
                tableBuilder.setRow(type.getKeyword(), ":",
                        category != null ? category.getName()
                                : TextLocalizer.localizeAll(WordsLocale.INVALID.tag, wrapper));
            }

            MessageSender.sendMessage("**__" + M_TYPE_LIST + "__**" + lineSeparator()
                    + tableBuilder, wrapper.getMessageChannel());
        } else if (args.length == 2) {
            //Return info for one ticket type.
            TicketType type = ticketData.getTypeByKeyword(wrapper.getGuild().get(), args[1], wrapper);

            if (type == null) {
                MessageSender.sendSimpleError(ErrorType.TYPE_NOT_FOUND, wrapper);
                return;
            }

            List<String> ownerMentions = parser.getRoles(wrapper.getGuild().get(),
                    ticketData.getTypeOwnerRoles(wrapper.getGuild().get(), type.getKeyword(), wrapper))
                    .stream().map(IMentionable::getAsMention).collect(Collectors.toList());

            List<String> supporterMentions = parser.getRoles(wrapper.getGuild().get(),
                    ticketData.getTypeSupportRoles(wrapper.getGuild().get(), type.getKeyword(), wrapper))
                    .stream().map(IMentionable::getAsMention).collect(Collectors.toList());

            List<LocalizedField> fields = new ArrayList<>();
            fields.add(new LocalizedField(M_CHANNEL_CATEGORY.tag, type.getCategory().getName(), false,
                    wrapper));
            fields.add(new LocalizedField(M_CREATION_MESSAGE.tag, type.getCreationMessage(), false,
                    wrapper));
            fields.add(new LocalizedField(M_TICKET_OWNER_ROLES.tag,
                    String.join(lineSeparator() + "", ownerMentions), false, wrapper));
            fields.add(new LocalizedField(M_TICKET_SUPPORT_ROLES.tag,
                    String.join(lineSeparator() + "", supporterMentions), false, wrapper));

            MessageSender.sendTextBox(M_TYPE_ABOUT + " **" + type.getKeyword() + "**",
                    fields, wrapper);
        }
    }

    private void open(String[] args, EventWrapper wrapper) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, wrapper);
            return;
        }

        Member member = parser.getGuildMember(wrapper.getGuild().get(), args[2]);
        if (member == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, wrapper);
            return;
        }

        if (Verifier.equalSnowflake(member, wrapper.getAuthor())) {
            MessageSender.sendSimpleError(ErrorType.TICKET_SELF_ASSIGNMENT, wrapper);
            return;
        }

        TicketType ticket = ticketData.getTypeByKeyword(wrapper.getGuild().get(), args[1], wrapper);

        if (ticket == null) {
            MessageSender.sendSimpleError(ErrorType.TYPE_NOT_FOUND, wrapper);
            return;
        }

        if (ticket.getCategory() == null) {
            MessageSender.sendMessage(TicketLocale.M_CATEGORY_NOT_FOUND.tag, wrapper.getMessageChannel());
            return;
        }

        //Set Channel Name
        String channelName = ticketData.getNextTicketCount(wrapper.getGuild().get(), wrapper)
                + " " + member.getUser().getName();

        //Create channel and wait for creation
        wrapper.getGuild().get()
                .createTextChannel(channelName)
                .setParent(ticket.getCategory())
                .queue(channel -> {
                    //Manage permissions for @everyone and deny read permission
                    Role everyone = wrapper.getGuild().get().getPublicRole();
                    ChannelManager manager = channel.getManager().getChannel().getManager();

                    manager.getChannel()
                            .upsertPermissionOverride(everyone)
                            .setDeny(Permission.MESSAGE_READ).queue();

                    //Gives ticket owner read permission in channel
                    manager.getChannel()
                            .upsertPermissionOverride(member)
                            .setAllow(Permission.MESSAGE_READ).queue();

                    //Saves channel in database

                    if (!ticketData.createChannel(wrapper.getGuild().get(), channel,
                            member.getUser(), ticket.getKeyword(), wrapper)) {
                        channel.delete().queue();
                        return;
                    }

                    //Get ticket support and owner roles
                    List<Role> supportRoles = parser.getRoles(wrapper.getGuild().get(),
                            ticketData.getTypeSupportRoles(wrapper.getGuild().get(),
                                    ticket.getKeyword(), wrapper));

                    List<Role> ownerRoles = parser.getRoles(wrapper.getGuild().get(),
                            ticketData.getTypeOwnerRoles(wrapper.getGuild().get(),
                                    ticket.getKeyword(), wrapper));
                    //Assign ticket support and owner roles
                    for (Role role : ownerRoles) {
                        wrapper.getGuild().get().addRoleToMember(member, role).queue();
                    }

                    for (Role role : supportRoles) {
                        manager.getChannel().upsertPermissionOverride(role)
                                .setAllow(Permission.MESSAGE_READ).queue();
                    }

                    //Greet ticket owner in ticket channel
                    MessageSender.sendMessageToChannel(
                            Replacer.applyUserPlaceholder(member.getUser(), ticket.getCreationMessage()),
                            channel);

                    MessageSender.sendMessage(localizeAllAndReplace(M_OPEN.tag, wrapper,
                            channel.getAsMention(), "**" + member.getEffectiveName() + "**"),
                            wrapper.getMessageChannel());
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
