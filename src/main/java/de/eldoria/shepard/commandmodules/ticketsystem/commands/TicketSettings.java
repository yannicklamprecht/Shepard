package de.eldoria.shepard.commandmodules.ticketsystem.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.ticketsystem.data.TicketData;
import de.eldoria.shepard.commandmodules.ticketsystem.util.TicketHelper;
import de.eldoria.shepard.commandmodules.ticketsystem.util.TicketType;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.util.Replacement;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.*;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.admin.TicketLocale.A_TICKET_TYPE;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketSettingsLocale.*;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static java.lang.System.lineSeparator;

/**
 * Command to configure the Ticket types on a guild.
 */
@CommandUsage(EventContext.GUILD)
public class TicketSettings extends Command implements Executable, ReqParser, ReqDataSource {
    private ArgumentParser parser;
    private TicketData ticketData;

    /**
     * Creates a new ticket setting command object.
     */
    public TicketSettings() {
        super("ticketSettings",
                new String[]{"ts"},
                DESCRIPTION.tag,
                SubCommand.builder("ticketSettings")
                        .addSubcommand(C_CREATE_TYPE.tag,
                                Parameter.createCommand("createType"),
                                Parameter.createInput(A_TICKET_TYPE.tag, null, true),
                                Parameter.createInput("command.general.argument.category", "command.general.argumentDescription.category", true))
                        .addSubcommand(C_REMOVE_TYPE.tag,
                                Parameter.createCommand("removeType"),
                                Parameter.createInput(A_TICKET_TYPE.tag, null, true))
                        .addSubcommand(C_SET_OWNER_ROLES.tag,
                                Parameter.createCommand("setOwnerRoles"),
                                Parameter.createInput(A_TICKET_TYPE.tag, null, true),
                                Parameter.createInput("command.general.argument.roles", "command.general.argumentDescription.roles", true))
                        .addSubcommand(C_SET_SUPPORT_ROLES.tag,
                                Parameter.createCommand("setSupportRoles"),
                                Parameter.createInput(A_TICKET_TYPE.tag, null, true),
                                Parameter.createInput("command.general.argument.roles", "command.general.argumentDescription.roles", true))
                        .addSubcommand(C_SET_CATEGORY.tag,
                                Parameter.createCommand("setChannelCategory"),
                                Parameter.createInput(A_TICKET_TYPE.tag, null, true),
                                Parameter.createInput("command.general.argument.category", "command.general.argumentDescription.category", true))
                        .addSubcommand(C_SET_CREATION_MESSAGE.tag,
                                Parameter.createCommand("setCreationMessage"),
                                Parameter.createInput(A_TICKET_TYPE.tag, null, true),
                                Parameter.createInput("command.general.argument.message", "command.general.argumentDescription.messageMention", true))
                        .build(),
                CommandCategory.ADMIN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String cmd = args[0];
        String type = args[1];
        Optional<TicketType> ticket = ticketData.getTypes(wrapper.getGuild().get(), wrapper).stream()
                .filter(ticketType -> ticketType.getKeyword().equalsIgnoreCase(type)).findFirst();

        SubCommand arg = subCommands[0];

        //All validation operations are inside the method except when they are needed for more than one method.
        if (isSubCommand(cmd, 0)) {
            if (ticket.isEmpty()) {
                createType(args, wrapper, type);
            } else {
                MessageSender.sendSimpleError(ErrorType.TYPE_ALREADY_DEFINED, wrapper);
            }
            return;
        }

        if (ticket.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.TYPE_NOT_FOUND, wrapper);
            return;
        }

        if (isSubCommand(cmd, 1)) {
            removeType(args, wrapper, ticket.get());
            return;
        }

        if (isSubCommand(cmd, 2) || isSubCommand(cmd, 3)) {
            setRoles(args, wrapper, cmd, ticket.get());
            return;
        }

        if (isSubCommand(cmd, 4) || cmd.equalsIgnoreCase("scc")) {
            setChannelCategory(args, wrapper, ticket.get());
            return;
        }

        if (isSubCommand(cmd, 5)) {
            setCreationMessage(args, wrapper, ticket.get());
            return;
        }
    }

    private void setCreationMessage(String[] args, EventWrapper wrapper, TicketType scopeTicket) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, wrapper);
            return;
        }

        String message = ArgumentParser.getMessage(args, 2);

        if (ticketData.setCreationMessage(wrapper.getGuild().get(), scopeTicket.getKeyword(), message,
                wrapper)) {
            MessageSender.sendSimpleTextBox(localizeAllAndReplace(M_SET_CREATION_MESSAGE.tag,
                    wrapper, scopeTicket.getKeyword()), message, wrapper);
        }
    }

    private void setChannelCategory(String[] args, EventWrapper messageContext, TicketType scopeTicket) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
            return;
        }

        Category category = messageContext.getGuild().get().getCategoryById(args[2]);

        if (category == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_CATEGORY, messageContext);
            return;
        }

        if (ticketData.addType(messageContext.getGuild().get(), category, null,
                scopeTicket.getKeyword(), messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_SET_CATEGORY.tag, messageContext,
                    "**" + scopeTicket.getKeyword() + "**", category.getName()),
                    messageContext.getMessageChannel());
        }
    }

    private void setRoles(String[] args, EventWrapper wrapper, String cmd, TicketType scopeTicket) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, wrapper);
            return;
        }

        List<Role> validRoles = parser.getRoles(wrapper.getGuild().get(),
                ArgumentParser.getRangeAsList(args, 2));

        for (Role role : validRoles) {
            if (!wrapper.getGuild().get().getSelfMember().canInteract(role)) {
                MessageSender.sendSimpleError(ErrorType.HIERARCHY_EXCEPTION, wrapper, Replacement.createMention(role));
                return;
            }
        }

        String roleMentions = validRoles.stream().map(IMentionable::getAsMention)
                .collect(Collectors.joining(lineSeparator()));

        if (isSubCommand(cmd, 2)) {
            if (ticketData.setTypeOwnerRoles(wrapper.getGuild().get(), scopeTicket.getKeyword(),
                    validRoles, wrapper)) {

                MessageSender.sendSimpleTextBox(M_SET_OWNER_ROLES.tag + " **"
                        + scopeTicket.getKeyword() + "**:", roleMentions, wrapper);
            }

        } else if (ticketData.setTypeSupportRoles(wrapper.getGuild().get(), scopeTicket.getKeyword(),
                validRoles, wrapper)) {
            MessageSender.sendSimpleTextBox(M_SET_SUPPORT_ROLES.tag + " **"
                    + scopeTicket.getKeyword() + "**:", roleMentions, wrapper);
        }

    }

    private void removeType(String[] args, EventWrapper wrapper, TicketType scopeTicket) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, wrapper);
            return;
        }
        List<TextChannel> validTextChannels = parser.getTextChannels(wrapper.getGuild().get(),
                ticketData.getChannelIdsByType(wrapper.getGuild().get(),
                        scopeTicket.getKeyword(), wrapper));

        List<Role> typeOwnerRoles = parser.getRoles(wrapper.getGuild().get(),
                ticketData.getTypeOwnerRoles(wrapper.getGuild().get(),
                        scopeTicket.getKeyword(), wrapper));


        Set<Member> members = new HashSet<>();

        for (TextChannel channel : validTextChannels) {

            Member member = parser.getGuildMember(wrapper.getGuild().get(),
                    ticketData.getChannelOwnerId(wrapper.getGuild().get(), channel, wrapper));

            if (member == null) continue;
            members.add(member);
        }

        for (Member member : members) {
            Role role = TicketHelper.removeAndUpdateTicketRoles(ticketData, parser, wrapper, member, typeOwnerRoles);
            if (role != null) {
                MessageSender.sendSimpleError(ErrorType.HIERARCHY_EXCEPTION, wrapper, Replacement.createMention(role));
                return;
            }
        }

        if (ticketData.removeTypeByKeyword(wrapper.getGuild().get(), scopeTicket.getKeyword(), wrapper)) {
            for (TextChannel channel : validTextChannels) {
                channel.delete().queue();
            }
            MessageSender.sendMessage(localizeAllAndReplace(M_REMOVE_TYPE.tag, wrapper.getGuild().get(),
                    "**" + scopeTicket.getKeyword() + "**"), wrapper.getMessageChannel());
        }
    }

    private void createType(String[] args, EventWrapper wrapper, String type) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, wrapper);
            return;
        }

        Category category = wrapper.getGuild().get().getCategoryById(args[2]);

        if (category == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_CATEGORY, wrapper);
            return;
        }

        if (ticketData.addType(wrapper.getGuild().get(), category, "", type, wrapper)) {
            MessageSender.sendMessage(M_CREATE_TYPE.tag + " **"
                    + type.toLowerCase() + "**", wrapper.getMessageChannel());
        }
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }

    @Override
    public void addDataSource(DataSource source) {
        ticketData = new TicketData(source);
    }
}