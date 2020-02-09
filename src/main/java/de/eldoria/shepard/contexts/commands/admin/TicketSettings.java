package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArgument;
import de.eldoria.shepard.contexts.commands.argument.SubArgument;
import de.eldoria.shepard.database.queries.TicketData;
import de.eldoria.shepard.database.types.TicketType;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_CATEGORY;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_MESSAGE_MENTION;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_NAME;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_ROLES;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketSettingsLocale.C_CREATE_TYPE;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketSettingsLocale.C_REMOVE_TYPE;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketSettingsLocale.C_SET_CATEGORY;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketSettingsLocale.C_SET_CREATION_MESSAGE;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketSettingsLocale.C_SET_OWNER_ROLES;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketSettingsLocale.C_SET_SUPPORT_ROLES;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketSettingsLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketSettingsLocale.M_CREATE_TYPE;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketSettingsLocale.M_REMOVE_TYPE;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketSettingsLocale.M_SET_CATEGORY;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketSettingsLocale.M_SET_CREATION_MESSAGE;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketSettingsLocale.M_SET_OWNER_ROLES;
import static de.eldoria.shepard.localization.enums.commands.admin.TicketSettingsLocale.M_SET_SUPPORT_ROLES;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static java.lang.System.lineSeparator;

/**
 * Command to configure the Ticket types on a guild.
 */
public class TicketSettings extends Command {
    /**
     * Creates a new ticket setting command object.
     */
    public TicketSettings() {
        commandName = "ticketSettings";
        commandDesc = DESCRIPTION.tag;
        commandAliases = new String[] {"ts"};
        commandArguments = new CommandArgument[] {
                new CommandArgument("action", true,
                        new SubArgument("createType", C_CREATE_TYPE.tag, true),
                        new SubArgument("removeType", C_REMOVE_TYPE.tag, true),
                        new SubArgument("setOwnerRoles", C_SET_OWNER_ROLES.tag, true),
                        new SubArgument("setSupportRoles", C_SET_SUPPORT_ROLES.tag, true),
                        new SubArgument("setChannelCategory", C_SET_CATEGORY.tag, true),
                        new SubArgument("setCreationMessage", C_SET_CREATION_MESSAGE.tag, true)),
                new CommandArgument("value", true,
                        new SubArgument("createType", A_NAME + " " + A_CATEGORY),
                        new SubArgument("removeType", A_NAME.tag),
                        new SubArgument("setOwnerRoles", A_NAME + " " + A_ROLES),
                        new SubArgument("setSupportRoles", A_NAME + " " + A_ROLES),
                        new SubArgument("setChannelCategory", A_NAME + " " + A_CATEGORY),
                        new SubArgument("setCreationMessage", A_NAME + " " + A_MESSAGE_MENTION))
        };
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        String type = args[1];
        Optional<TicketType> ticket = TicketData.getTypes(messageContext.getGuild(), messageContext).stream()
                .filter(ticketType -> ticketType.getKeyword().equalsIgnoreCase(type)).findFirst();

        CommandArgument arg = commandArguments[0];

        //All validation operations are inside the method except when they are needed for more than one method.
        if (arg.isSubCommand(cmd, 0)) {
            if (ticket.isEmpty()) {
                createType(args, messageContext, type);
            } else {
                MessageSender.sendSimpleError(ErrorType.TYPE_ALREADY_DEFINED, messageContext.getTextChannel());
            }
            return;
        }

        if (ticket.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.TYPE_NOT_FOUND, messageContext.getTextChannel());
            return;
        }

        if (arg.isSubCommand(cmd, 1)) {
            removeType(args, messageContext, ticket.get());
            return;
        }

        if (arg.isSubCommand(cmd, 2) || arg.isSubCommand(cmd, 3)) {
            setRoles(args, messageContext, cmd, ticket.get());
            return;
        }

        if (arg.isSubCommand(cmd, 4) || cmd.equalsIgnoreCase("scc")) {
            setChannelCategory(args, messageContext, ticket.get());
            return;
        }

        if (arg.isSubCommand(cmd, 5)) {
            setCreationMessage(args, messageContext, ticket.get());
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
    }

    private void setCreationMessage(String[] args, MessageEventDataWrapper messageContext, TicketType scopeTicket) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }

        String message = ArgumentParser.getMessage(args, 2);

        if (TicketData.setCreationMessage(messageContext.getGuild(), scopeTicket.getKeyword(), message,
                messageContext)) {
            MessageSender.sendSimpleTextBox(localizeAllAndReplace(M_SET_CREATION_MESSAGE.tag,
                    messageContext.getGuild(), scopeTicket.getKeyword()), message, messageContext.getTextChannel());
        }
    }

    private void setChannelCategory(String[] args, MessageEventDataWrapper messageContext, TicketType scopeTicket) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }

        Category category = messageContext.getGuild().getCategoryById(args[2]);

        if (category == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_CATEGORY, messageContext.getTextChannel());
            return;
        }

        if (TicketData.addType(messageContext.getGuild(), category, null,
                scopeTicket.getKeyword(), messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_SET_CATEGORY.tag, messageContext.getGuild(),
                    "**" + scopeTicket.getKeyword() + "**", category.getName()), messageContext.getTextChannel());
        }
    }

    private void setRoles(String[] args, MessageEventDataWrapper messageContext, String cmd, TicketType scopeTicket) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }

        List<Role> validRoles = ArgumentParser.getRoles(messageContext.getGuild(),
                ArgumentParser.getRangeAsList(args, 2));

        String roleMentions = validRoles.stream().map(IMentionable::getAsMention)
                .collect(Collectors.joining(lineSeparator()));

        if (commandArguments[0].isSubCommand(cmd, 2)) {
            if (TicketData.setTypeOwnerRoles(messageContext.getGuild(), scopeTicket.getKeyword(),
                    validRoles, messageContext)) {

                MessageSender.sendSimpleTextBox(M_SET_OWNER_ROLES.tag + " **"
                        + scopeTicket.getKeyword() + "**:", roleMentions, messageContext.getTextChannel());
            }

        } else if (TicketData.setTypeSupportRoles(messageContext.getGuild(), scopeTicket.getKeyword(),
                validRoles, messageContext)) {
            MessageSender.sendSimpleTextBox(M_SET_SUPPORT_ROLES.tag + " **"
                    + scopeTicket.getKeyword() + "**:", roleMentions, messageContext.getTextChannel());
        }

    }

    private void removeType(String[] args, MessageEventDataWrapper messageContext, TicketType scopeTicket) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }
        List<TextChannel> validTextChannels = ArgumentParser.getTextChannels(messageContext.getGuild(),
                TicketData.getChannelIdsByType(messageContext.getGuild(),
                        scopeTicket.getKeyword(), messageContext));

        List<Role> typeOwnerRoles = ArgumentParser.getRoles(messageContext.getGuild(),
                TicketData.getTypeOwnerRoles(messageContext.getGuild(),
                        scopeTicket.getKeyword(), messageContext));


        Set<Member> members = new HashSet<>();

        for (TextChannel channel : validTextChannels) {

            Member member = ArgumentParser.getGuildMember(messageContext.getGuild(),
                    TicketData.getChannelOwnerId(messageContext.getGuild(), channel, messageContext));

            if (member == null) continue;
            members.add(member);
        }

        for (Member member : members) {
            TicketHelper.removeAndUpdateTicketRoles(messageContext, member, typeOwnerRoles);
        }

        if (TicketData.removeTypeByKeyword(messageContext.getGuild(), scopeTicket.getKeyword(), messageContext)) {
            for (TextChannel channel : validTextChannels) {
                channel.delete().queue();
            }
            MessageSender.sendMessage(localizeAllAndReplace(M_REMOVE_TYPE.tag, messageContext.getGuild(),
                    "**" + scopeTicket.getKeyword() + "**"), messageContext.getTextChannel());
        }
    }

    private void createType(String[] args, MessageEventDataWrapper messageContext, String type) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }

        Category category = messageContext.getGuild().getCategoryById(args[2]);

        if (category == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_CATEGORY, messageContext.getTextChannel());
            return;
        }

        if (TicketData.addType(messageContext.getGuild(), category, "", type, messageContext)) {
            MessageSender.sendMessage(M_CREATE_TYPE.tag + " **"
                    + type.toLowerCase() + "**", messageContext.getTextChannel());
        }
    }
}