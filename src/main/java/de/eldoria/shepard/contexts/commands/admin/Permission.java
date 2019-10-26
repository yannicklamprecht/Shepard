package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.contexts.commands.botconfig.enums.ModifyType;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.localization.enums.GeneralLocale;
import de.eldoria.shepard.localization.enums.admin.PermissionLocale;
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

import static de.eldoria.shepard.localization.enums.GeneralLocale.*;
import static de.eldoria.shepard.localization.enums.admin.PermissionLocale.*;
import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class Permission extends Command {
    /**
     * Creates a new permission command object.
     */
    public Permission() {
        commandName = "permission";
        commandDesc = DESCRIPTION.replacement;
        commandArgs = new CommandArg[] {
                new CommandArg("context name", true,
                        new SubArg("context name", A_CONTEXT_NAME.replacement)),
                new CommandArg("action", true,
                        new SubArg("addUser", C_ADD_USER.replacement, true),
                        new SubArg("removeUser", C_REMOVE_USER.replacement, true),
                        new SubArg("listUser", C_LIST_USER.replacement, true),
                        new SubArg("addRole", C_ADD_ROLE.replacement, true),
                        new SubArg("removeRole", C_REMOVE_ROLE.replacement, true),
                        new SubArg("listRoles", C_LIST_ROLE.replacement, true)),
                new CommandArg("value", false,
                        new SubArg("addUser", A_USERS.replacement),
                        new SubArg("removeUser", A_USERS.replacement),
                        new SubArg("listUser", A_EMPTY.replacement),
                        new SubArg("addRole", A_ROLES.replacement),
                        new SubArg("removeRole", A_ROLES.replacement),
                        new SubArg("listRoles", A_EMPTY.replacement)),
        };
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[1];

        String contextName = ArgumentParser.getContextName(args[0], messageContext);

        if (contextName == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND,
                    messageContext);
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

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext);
    }

    private void showMentions(MessageEventDataWrapper messageContext, String contextName, String message) {
        String roleMentions = ArgumentParser.getRoles(messageContext.getGuild(),
                ContextData.getContextRolePermission(messageContext.getGuild(),
                        contextName, messageContext)).stream().map(IMentionable::getAsMention)
                .collect(Collectors.joining(lineSeparator()));
        MessageSender.sendSimpleTextBox(message,
                roleMentions,
                Color.blue, messageContext);
    }

    private void modifyUsers(String[] args, MessageEventDataWrapper receivedEvent,
                             String contextName, ModifyType modifyType) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, receivedEvent);
            return;
        }


        List<User> validUser = ArgumentParser.getGuildUsers(receivedEvent.getGuild(),
                ArgumentParser.getRangeAsList(Arrays.asList(args), 2));

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
            MessageSender.sendSimpleTextBox("Granted following users access to context **"
                            + contextName.toUpperCase() + "**", names, Color.green,
                    receivedEvent);
        } else {
            MessageSender.sendSimpleTextBox("Revoked access from following users for context **"
                            + contextName.toUpperCase() + "**", names, Color.red,
                    receivedEvent);
        }
    }

    private void modifyRoles(String[] args, MessageEventDataWrapper messageContext,
                             String contextName, ModifyType modifyType) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext);
            return;
        }

        List<Role> roles = ArgumentParser.getRoles(messageContext.getGuild(), ArgumentParser.getRangeAsList(args, 2));

        for (Role role : roles) {
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

        String names = roles.stream().map(IMentionable::getAsMention).collect(Collectors.joining(lineSeparator()));

        if (modifyType == ModifyType.ADD) {
            MessageSender.sendSimpleTextBox("Granted following roles access to context **"
                    + contextName.toUpperCase() + "**", names, messageContext);
        } else {
            MessageSender.sendSimpleTextBox("Revoked access from following roles for context **"
                    + contextName.toUpperCase() + "**", names, messageContext);
        }
    }
}
