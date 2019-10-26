package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.TicketData;
import de.eldoria.shepard.database.types.TicketType;
import de.eldoria.shepard.localization.enums.GeneralLocale;
import de.eldoria.shepard.localization.enums.admin.TicketSettingsLocale;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.GeneralLocale.*;
import static de.eldoria.shepard.localization.enums.admin.TicketSettingsLocale.*;
import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class TicketSettings extends Command {
    /**
     * Creates a new ticket setting command object.
     */
    public TicketSettings() {
        commandName = "ticketSettings";
        commandDesc = DESCRIPTION.replacement;
        commandAliases = new String[] {"ts"};
        commandArgs = new CommandArg[] {
                new CommandArg("action", true,
                        new SubArg("createType", C_CREATE_TYPE.replacement, true),
                        new SubArg("removeType", C_REMOVE_TYPE.replacement, true),
                        new SubArg("setOwnerRoles", C_SET_OWNER_ROLES.replacement, true),
                        new SubArg("setSupportRoles", C_SET_SUPPORT_ROLES.replacement, true),
                        new SubArg("setChannelCategory", C_SET_CATEGORY.replacement, true),
                        new SubArg("setCreationMessage", C_SET_CREATION_MESSAGE.replacement, true)),
                new CommandArg("value", true,
                        new SubArg("createType", A_NAME + " " + A_CATEGORY),
                        new SubArg("removeType", A_NAME.replacement),
                        new SubArg("setOwnerRoles", A_NAME + " " + A_ROLES),
                        new SubArg("setSupportRoles", A_NAME + " " + A_ROLES),
                        new SubArg("setChannelCategory", A_NAME + " " + A_CATEGORY),
                        new SubArg("setCreationMessage", A_NAME + " " + A_MESSAGE_MENTION))
        };
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        String type = args[1];
        Optional<TicketType> ticket = TicketData.getTypes(messageContext.getGuild(), messageContext).stream()
                .filter(ticketType -> ticketType.getKeyword().equalsIgnoreCase(type)).findFirst();

        //All validation operations are inside the method except when they are needed for more than one method.
        if (isArgument(cmd, "createType", "ct")) {
            if (ticket.isEmpty()) {
                createType(args, messageContext, type);
            } else {
                MessageSender.sendSimpleError(ErrorType.TYPE_ALREADY_DEFINED, messageContext);
            }
            return;
        }

        if (ticket.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.TYPE_NOT_FOUND, messageContext);
            return;
        }

        if (isArgument(cmd, "removeType", "rt")) {
            removeType(args, messageContext, ticket.get());
            return;
        }

        if (isArgument(cmd, "setOwnerRoles", "sor", "setSupportRoles", "ssr")) {
            setRoles(args, messageContext, cmd, ticket.get());
            return;
        }

        if (isArgument("setChannelCategory") || cmd.equalsIgnoreCase("scc")) {
            setChannelCategory(args, messageContext, ticket.get());
            return;
        }

        if (isArgument(cmd, "setCreationMessage", "scm")) {
            setCreationMessage(args, messageContext, ticket.get());
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
    }

    private void setCreationMessage(String[] args, MessageEventDataWrapper receivedEvent, TicketType scopeTicket) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, receivedEvent);
            return;
        }

        String message = ArgumentParser.getMessage(args, 2);

        if (TicketData.setCreationMessage(receivedEvent.getGuild(), scopeTicket.getKeyword(), message,
                receivedEvent)) {
            MessageSender.sendSimpleTextBox("Set creation text for ticket type " + scopeTicket.getKeyword() + " to:",
                    message, receivedEvent);
        }
    }

    private void setChannelCategory(String[] args, MessageEventDataWrapper messageContext, TicketType scopeTicket) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
            return;
        }

        Category category = messageContext.getGuild().getCategoryById(args[2]);

        if (category == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_CATEGORY, messageContext);
            return;
        }

        if (TicketData.addType(messageContext.getGuild(), category, null,
                scopeTicket.getKeyword(), messageContext)) {
            MessageSender.sendMessage(locale.getReplacedString(M_SET_CATEGORY.localeCode, messageContext.getGuild(),
                    "**" + scopeTicket.getKeyword() + "**", category.getName()), messageContext);
        }
    }

    private void setRoles(String[] args, MessageEventDataWrapper receivedEvent, String cmd, TicketType scopeTicket) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, receivedEvent);
            return;
        }

        List<Role> validRoles = ArgumentParser.getRoles(receivedEvent.getGuild(),
                ArgumentParser.getRangeAsList(args, 2));

        String roleMentions = validRoles.stream().map(IMentionable::getAsMention)
                .collect(Collectors.joining(lineSeparator()));

        if (cmd.equalsIgnoreCase("setOwnerRoles") || cmd.equalsIgnoreCase("sor")) {
            if (TicketData.setTypeOwnerRoles(receivedEvent.getGuild(), scopeTicket.getKeyword(),
                    validRoles, receivedEvent)) {

                MessageSender.sendSimpleTextBox(M_SET_OWNER_ROLES.replacement + " **"
                                + scopeTicket.getKeyword() + "**:", roleMentions,
                        receivedEvent);
            }

        } else if (TicketData.setTypeSupportRoles(receivedEvent.getGuild(), scopeTicket.getKeyword(),
                validRoles, receivedEvent)) {
            MessageSender.sendSimpleTextBox(M_SET_SUPPORT_ROLES.replacement + " **"
                            + scopeTicket.getKeyword() + "**:", roleMentions,
                    receivedEvent);
        }

    }

    private void removeType(String[] args, MessageEventDataWrapper messageContext, TicketType scopeTicket) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
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
            MessageSender.sendMessage(locale.getReplacedString(M_REMOVE_TYPE.localeCode, messageContext.getGuild(),
                    "**" + scopeTicket.getKeyword() + "**"),
                    messageContext);
        }
    }

    private void createType(String[] args, MessageEventDataWrapper receivedEvent, String type) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, receivedEvent);
            return;
        }

        Category category = receivedEvent.getGuild().getCategoryById(args[2]);

        if (category == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_CATEGORY, receivedEvent);
            return;
        }

        if (TicketData.addType(receivedEvent.getGuild(), category, "", type, receivedEvent)) {
            MessageSender.sendMessage(M_CREATE_TYPE.replacement + " **"
                    + type.toLowerCase() + "**", receivedEvent);
        }
    }
}
