package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.contexts.commands.botconfig.enums.ModifyType;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.Verifier;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.contexts.ContextHelper.getContextName;
import static java.lang.System.lineSeparator;

public class Permission extends Command {
    /**
     * Creates a new permission command object.
     */
    public Permission() {
        commandName = "permission";
        commandDesc = "Manage which context can be used by users or roles.";
        commandArgs = new CommandArg[] {
                new CommandArg("context name",
                        "name or alias of the context u want to manage",
                        true),
                new CommandArg("action",
                        "**__a__dd__U__ser** -> Gives a user access to this context." + lineSeparator()
                                + "**__r__emove__U__ser** -> Revokes access to a context for a user." + lineSeparator()
                                + "**__s__how__U__ser** -> List of users with access to this context." + lineSeparator()
                                + "**__a__dd__R__ole** -> Gives a role access to this context." + lineSeparator()
                                + "**__r__emove__R__ole** -> Revokes access to a context for a role." + lineSeparator()
                                + "**__s__how__R__oles** -> List of roles with access to this context.",
                        true),
                new CommandArg("value",
                        "**addUser** -> [user...] one or more usernames" + lineSeparator()
                                + "**removeUser** -> [user...] one or more usernames." + lineSeparator()
                                + "**showUser** -> leave empty." + lineSeparator()
                                + "**addRole** -> [role...] one or more roles." + lineSeparator()
                                + "**removeRole** -> [role...] one or more roles." + lineSeparator()
                                + "**showRoles** -> leave empty.",
                        false)
        };
    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String cmd = args[1];

        String contextName = getContextName(args[0], receivedEvent);

        if (contextName == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND,
                    receivedEvent.getChannel());
            return;
        }

        if (cmd.equalsIgnoreCase("addUser") || cmd.equalsIgnoreCase("au")
                || cmd.equalsIgnoreCase("removeUser") || cmd.equalsIgnoreCase("ru")) {
            ModifyType modifyType = cmd.equalsIgnoreCase("addUser") || cmd.equalsIgnoreCase("au")
                    ? ModifyType.ADD : ModifyType.REMOVE;

            modifyUsers(args, receivedEvent, contextName, modifyType);
            return;
        }

        if (cmd.equalsIgnoreCase("showUser") || cmd.equalsIgnoreCase("su")) {
            showMentions(receivedEvent, contextName, "Users with access to this context:");
            return;
        }

        if (cmd.equalsIgnoreCase("addRole") || cmd.equalsIgnoreCase("ar")
                || cmd.equalsIgnoreCase("removeRole") || cmd.equalsIgnoreCase("rr")) {
            ModifyType modifyType = cmd.equalsIgnoreCase("addRole") || cmd.equalsIgnoreCase("ar")
                    ? ModifyType.ADD : ModifyType.REMOVE;

            modifyRoles(args, receivedEvent, contextName, modifyType);
            return;
        }

        if (cmd.equalsIgnoreCase("showRole") || cmd.equalsIgnoreCase("sr")) {
            showMentions(receivedEvent, contextName, "Roles with access to this context:");
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, receivedEvent.getChannel());
    }

    private void showMentions(MessageReceivedEvent receivedEvent, String contextName, String message) {
        List<String> contextRolePermission =
                ContextData.getContextRolePermission(receivedEvent.getGuild(), contextName, receivedEvent);
        MessageSender.sendSimpleTextBox(message,
                Verifier.getValidRoles(receivedEvent.getGuild(), contextRolePermission)
                        .stream().map(IMentionable::getAsMention).collect(Collectors.joining(lineSeparator())),
                Color.blue, receivedEvent.getChannel());
    }

    private void modifyUsers(String[] args, MessageReceivedEvent receivedEvent,
                             String contextName, ModifyType modifyType) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, receivedEvent.getChannel());
            return;
        }

        List<User> validUser = Verifier.getValidUser(receivedEvent.getGuild(),
                Arrays.copyOfRange(args, 2, args.length));

        for (User user : validUser) {
            if (modifyType == ModifyType.ADD) {
                ContextData.addContextUserPermission(contextName, receivedEvent.getGuild(), user, receivedEvent);
            } else {
                ContextData.removeContextUserPermission(contextName, receivedEvent.getGuild(), user, receivedEvent);
            }
        }

        String names = validUser.stream().map(IMentionable::getAsMention).collect(Collectors.joining(lineSeparator()));

        if (modifyType == ModifyType.ADD) {
            MessageSender.sendSimpleTextBox("Granted following users access to context \""
                            + contextName.toUpperCase() + "\"", names + "**", Color.green,
                    receivedEvent.getChannel());
        } else {
            MessageSender.sendSimpleTextBox("Revoked access from following users for context \""
                            + contextName.toUpperCase() + "\"", names + "**", Color.red,
                    receivedEvent.getChannel());
        }
    }

    private void modifyRoles(String[] args, MessageReceivedEvent receivedEvent,
                             String contextName, ModifyType modifyType) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, receivedEvent.getChannel());
            return;
        }

        List<Role> validRoles = Verifier.getValidRoles(receivedEvent.getGuild(),
                Arrays.copyOfRange(args, 2, args.length));

        for (Role role : validRoles) {
            if (modifyType == ModifyType.ADD) {
                ContextData.addContextRolePermission(contextName, receivedEvent.getGuild(), role, receivedEvent);
            } else {
                ContextData.removeContextRolePermission(contextName, receivedEvent.getGuild(), role, receivedEvent);
            }
        }

        String names = validRoles.stream().map(IMentionable::getAsMention).collect(Collectors.joining(lineSeparator()));

        if (modifyType == ModifyType.ADD) {
            MessageSender.sendSimpleTextBox("Granted following roles access to context \""
                            + contextName.toUpperCase() + "\"", names + "**",
                    receivedEvent.getChannel());
        } else {
            MessageSender.sendSimpleTextBox("Revoked access from following roles for context \""
                            + contextName.toUpperCase() + "\"", names + "**",
                    receivedEvent.getChannel());
        }
    }
}
