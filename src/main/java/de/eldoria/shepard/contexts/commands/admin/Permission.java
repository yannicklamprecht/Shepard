package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.contexts.commands.botconfig.enums.ModifyType;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.Verifier;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.contexts.ContextHelper.getContextName;
import static de.eldoria.shepard.util.Verifier.isArgument;
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
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[1];

        String contextName = getContextName(args[0], messageContext);

        if (contextName == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND,
                    messageContext.getChannel());
            return;
        }

        if (isArgument(cmd, "addUser", "au", "removeUser", "ru")) {
            ModifyType modifyType = cmd.equalsIgnoreCase("addUser") || cmd.equalsIgnoreCase("au")
                    ? ModifyType.ADD : ModifyType.REMOVE;

            modifyUsers(args, messageContext, contextName, modifyType);
            return;
        }

        if (isArgument(cmd, "showUser", "su")) {
            showMentions(messageContext, contextName, "Users with access to this context:");
            return;
        }

        if (isArgument(cmd, "addRole", "ar", "removeRole", "rr")) {
            ModifyType modifyType = cmd.equalsIgnoreCase("addRole") || cmd.equalsIgnoreCase("ar")
                    ? ModifyType.ADD : ModifyType.REMOVE;

            modifyRoles(args, messageContext, contextName, modifyType);
            return;
        }

        if (isArgument(cmd, "showRole", "sr")) {
            showMentions(messageContext, contextName, "Roles with access to this context:");
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getChannel());
    }

    private void showMentions(MessageEventDataWrapper messageContext, String contextName, String message) {
        List<String> contextRolePermission = ContextData.getContextRolePermission(messageContext.getGuild(),
                contextName, messageContext);
        MessageSender.sendSimpleTextBox(message,
                Verifier.getValidRoles(messageContext.getGuild(), contextRolePermission)
                        .stream().map(IMentionable::getAsMention).collect(Collectors.joining(lineSeparator())),
                Color.blue, messageContext.getChannel());
    }

    private void modifyUsers(String[] args, MessageEventDataWrapper receivedEvent,
                             String contextName, ModifyType modifyType) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, receivedEvent.getChannel());
            return;
        }

        List<User> validUser = Verifier.getValidUserByString(Arrays.copyOfRange(args, 2, args.length));

        for (User user : validUser) {
            if (modifyType == ModifyType.ADD) {
                if (!ContextData.addContextUserPermission(contextName,
                        receivedEvent.getGuild(), user, receivedEvent)) {
                    return;
                }
            } else {
                if (!ContextData.removeContextUserPermission(contextName,
                        receivedEvent.getGuild(), user, receivedEvent)) {
                    return;
                }
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

    private void modifyRoles(String[] args, MessageEventDataWrapper messageContext,
                             String contextName, ModifyType modifyType) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getChannel());
            return;
        }

        List<Role> validRoles = Verifier.getValidRoles(messageContext.getGuild(),
                Arrays.copyOfRange(args, 2, args.length));

        for (Role role : validRoles) {
            if (modifyType == ModifyType.ADD) {
                if (!ContextData.addContextRolePermission(contextName,
                        messageContext.getGuild(), role, messageContext)) {
                    return;
                }
            } else {
                if (!ContextData.removeContextRolePermission(contextName,
                        messageContext.getGuild(), role, messageContext)) {
                    return;
                }
            }
        }

        String names = validRoles.stream().map(IMentionable::getAsMention).collect(Collectors.joining(lineSeparator()));

        if (modifyType == ModifyType.ADD) {
            MessageSender.sendSimpleTextBox("Granted following roles access to context \""
                            + contextName.toUpperCase() + "\"", names + "**",
                    messageContext.getChannel());
        } else {
            MessageSender.sendSimpleTextBox("Revoked access from following roles for context \""
                            + contextName.toUpperCase() + "\"", names + "**",
                    messageContext.getChannel());
        }
    }
}
