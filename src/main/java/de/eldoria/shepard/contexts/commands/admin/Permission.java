package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.contexts.commands.botconfig.enums.ModifyType;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_CONTEXT_NAME;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_EMPTY;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_ROLES;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_USERS;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_ADD_ROLE;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_ADD_USER;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_LIST_ROLE;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_LIST_USER;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_REMOVE_ROLE;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_REMOVE_USER;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_ROLE_ACCESS;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_ROLE_ACCESS_GRANTED;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_ROLE_ACCESS_REVOKED;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_USER_ACCESS;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_USER_ACCESS_GRANTED;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_USER_ACCESS_REVOKED;
import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class Permission extends Command {
    /**
     * Creates a new permission command object.
     */
    public Permission() {
        commandName = "permission";
        commandDesc = DESCRIPTION.tag;
        commandArgs = new CommandArg[] {
                new CommandArg("context name", true,
                        new SubArg("context name", A_CONTEXT_NAME.tag)),
                new CommandArg("action", true,
                        new SubArg("addUser", C_ADD_USER.tag, true),
                        new SubArg("removeUser", C_REMOVE_USER.tag, true),
                        new SubArg("listUser", C_LIST_USER.tag, true),
                        new SubArg("addRole", C_ADD_ROLE.tag, true),
                        new SubArg("removeRole", C_REMOVE_ROLE.tag, true),
                        new SubArg("listRoles", C_LIST_ROLE.tag, true)),
                new CommandArg("value", false,
                        new SubArg("addUser", A_USERS.tag),
                        new SubArg("removeUser", A_USERS.tag),
                        new SubArg("listUser", A_EMPTY.tag),
                        new SubArg("addRole", A_ROLES.tag),
                        new SubArg("removeRole", A_ROLES.tag),
                        new SubArg("listRoles", A_EMPTY.tag)),
        };
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[1];

        String contextName = ArgumentParser.getContextName(args[0], messageContext);

        if (contextName == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND,
                    messageContext.getTextChannel());
            return;
        }

        if (isArgument(cmd, "addUser", "au", "removeUser", "ru")) {
            ModifyType modifyType = cmd.equalsIgnoreCase("addUser") || cmd.equalsIgnoreCase("au")
                    ? ModifyType.ADD : ModifyType.REMOVE;

            modifyUsers(args, messageContext, contextName, modifyType);
            return;
        }

        if (isArgument(cmd, "showUser", "su")) {
            showMentions(messageContext, contextName, M_USER_ACCESS.tag);
            return;
        }

        if (isArgument(cmd, "addRole", "ar", "removeRole", "rr")) {
            ModifyType modifyType = cmd.equalsIgnoreCase("addRole") || cmd.equalsIgnoreCase("ar")
                    ? ModifyType.ADD : ModifyType.REMOVE;

            modifyRoles(args, messageContext, contextName, modifyType);
            return;
        }

        if (isArgument(cmd, "showRole", "sr")) {
            showMentions(messageContext, contextName, M_ROLE_ACCESS.tag);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
    }

    private void showMentions(MessageEventDataWrapper messageContext, String contextName, String message) {
        String roleMentions = ArgumentParser.getRoles(messageContext.getGuild(),
                ContextData.getContextRolePermission(messageContext.getGuild(),
                        contextName, messageContext)).stream().map(IMentionable::getAsMention)
                .collect(Collectors.joining(lineSeparator()));
        MessageSender.sendSimpleTextBox(message,
                roleMentions,
                Color.blue, messageContext.getTextChannel());
    }

    private void modifyUsers(String[] args, MessageEventDataWrapper messageContext,
                             String contextName, ModifyType modifyType) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            return;
        }


        List<User> validUser = ArgumentParser.getGuildUsers(messageContext.getGuild(),
                ArgumentParser.getRangeAsList(Arrays.asList(args), 2));

        for (User user : validUser) {
            if (modifyType == ModifyType.ADD) {
                if (!ContextData.addContextUserPermission(contextName,
                        messageContext.getGuild(), user, messageContext)) {
                    return;
                }
            } else {
                if (!ContextData.removeContextUserPermission(contextName,
                        messageContext.getGuild(), user, messageContext)) {
                    return;
                }
            }
        }

        sendMessage(messageContext, contextName, modifyType,
                M_USER_ACCESS_GRANTED, M_USER_ACCESS_REVOKED, new ArrayList<>(validUser));
    }

    private void modifyRoles(String[] args, MessageEventDataWrapper messageContext,
                             String contextName, ModifyType modifyType) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
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

        sendMessage(messageContext, contextName, modifyType,
                M_ROLE_ACCESS_GRANTED, M_ROLE_ACCESS_REVOKED, new ArrayList<>(roles));
    }

    private void sendMessage(MessageEventDataWrapper messageContext, String contextName, ModifyType modifyType,
                             PermissionLocale grantedMessage, PermissionLocale revokedMessage,
                             List<IMentionable> mentions) {
        String names = mentions.stream().map(IMentionable::getAsMention).collect(Collectors.joining(lineSeparator()));

        if (modifyType == ModifyType.ADD) {
            MessageSender.sendSimpleTextBox(grantedMessage + " **" + contextName.toUpperCase() + "**",
                    names, Color.green, messageContext.getTextChannel());
        } else {
            MessageSender.sendSimpleTextBox(revokedMessage + " **" + contextName.toUpperCase() + "**",
                    names, Color.red, messageContext.getTextChannel());
        }
    }
}
