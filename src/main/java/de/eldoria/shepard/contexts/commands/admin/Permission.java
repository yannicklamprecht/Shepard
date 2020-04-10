package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.ContextSensitive;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArgument;
import de.eldoria.shepard.contexts.commands.argument.SubArgument;
import de.eldoria.shepard.contexts.commands.botconfig.enums.ModifyType;
import de.eldoria.shepard.database.queries.commands.ContextData;
import de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_BOOLEAN;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_CONTEXT_NAME;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_EMPTY;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_ROLES;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_USERS;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_ADD_ROLE;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_ADD_USER;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_INFO;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_LIST_ROLE;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_LIST_USER;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_REMOVE_ROLE;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_REMOVE_USER;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_SET_PERMISSION_OVERRIDE;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_ADMIN_ONLY;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_GENERAL_INFORMATION;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_INFO_TITLE;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_OVERRIDE_ACTIVE;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_PERMISSION_REQUIRED;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_ROLES_WITH_PERMISSION;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_ROLE_ACCESS;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_ROLE_ACCESS_GRANTED;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_ROLE_ACCESS_REVOKED;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_USER_ACCESS;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_USER_ACCESS_GRANTED;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_USER_ACCESS_REVOKED;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_USER_WITH_PERMISSION;
import static java.lang.System.lineSeparator;

public class Permission extends Command {
    /**
     * Creates a new permission command object.
     */
    public Permission() {
        commandName = "permission";
        commandAliases = new String[] {"perm"};
        commandDesc = DESCRIPTION.tag;
        commandArguments = new CommandArgument[] {
                new CommandArgument("context name", true,
                        new SubArgument("context name", A_CONTEXT_NAME.tag)),
                new CommandArgument("action", true,
                        new SubArgument("addUser", C_ADD_USER.tag, true),
                        new SubArgument("removeUser", C_REMOVE_USER.tag, true),
                        new SubArgument("listUser", C_LIST_USER.tag, true),
                        new SubArgument("addRole", C_ADD_ROLE.tag, true),
                        new SubArgument("removeRole", C_REMOVE_ROLE.tag, true),
                        new SubArgument("listRoles", C_LIST_ROLE.tag, true),
                        new SubArgument("setPermissionOverride", C_SET_PERMISSION_OVERRIDE.tag, true),
                        new SubArgument("info", C_INFO.tag, true)),
                new CommandArgument("value", false,
                        new SubArgument("addUser", A_USERS.tag),
                        new SubArgument("removeUser", A_USERS.tag),
                        new SubArgument("listUser", A_EMPTY.tag),
                        new SubArgument("addRole", A_ROLES.tag),
                        new SubArgument("removeRole", A_ROLES.tag),
                        new SubArgument("listRoles", A_EMPTY.tag),
                        new SubArgument("setPermissionOverride", A_BOOLEAN.tag),
                        new SubArgument("info", A_EMPTY.tag)),
        };
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[1];

        ContextSensitive context = ArgumentParser.getContext(args[0], messageContext);

        if (context == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND,
                    messageContext.getTextChannel());
            return;
        }

        CommandArgument arg = commandArguments[1];

        if (arg.isSubCommand(cmd, 0)) {
            modifyUsers(args, messageContext, context, ModifyType.ADD);
            return;
        }
        if (arg.isSubCommand(cmd, 1)) {
            modifyUsers(args, messageContext, context, ModifyType.REMOVE);
            return;
        }

        if (arg.isSubCommand(cmd, 2)) {
            showMentions(messageContext, context, M_USER_ACCESS.tag);
            return;
        }

        if (arg.isSubCommand(cmd, 3)) {
            modifyRoles(args, messageContext, context, ModifyType.ADD);
            return;
        }
        if (arg.isSubCommand(cmd, 4)) {
            modifyRoles(args, messageContext, context, ModifyType.REMOVE);
            return;
        }

        if (arg.isSubCommand(cmd, 5)) {
            showMentions(messageContext, context, M_ROLE_ACCESS.tag);
            return;
        }

        if (arg.isSubCommand(cmd, 6)) {
            overridePermission(args, messageContext, context);
            return;
        }

        if (arg.isSubCommand(cmd, 7)) {
            info(messageContext, context);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
    }

    private void info(MessageEventDataWrapper messageContext, ContextSensitive context) {
        LocalizedEmbedBuilder embedBuilder = new LocalizedEmbedBuilder(messageContext)
                .setTitle(TextLocalizer.localizeAllAndReplace(M_INFO_TITLE.tag, messageContext.getGuild(),
                        context.getContextName()));

        String builder = M_ADMIN_ONLY + " " + context.isAdmin() + lineSeparator()
                + M_OVERRIDE_ACTIVE + " " + context.overrideActive(messageContext.getGuild()) + lineSeparator()
                + M_PERMISSION_REQUIRED + " " + context.needsPermission(messageContext.getGuild());
        embedBuilder.addField(M_GENERAL_INFORMATION.tag, builder, false);

        if (context.needsPermission(messageContext.getGuild())) {
            List<Role> roles = context.getRolesWithPermissions(messageContext);
            List<User> users = context.getUsersWithPermissions(messageContext);

            if (!roles.isEmpty()) {
                String roleMentions = roles.stream()
                        .map(IMentionable::getAsMention).collect(Collectors.joining(lineSeparator()));
                embedBuilder.addField(M_ROLES_WITH_PERMISSION.tag, roleMentions, false);
            }

            if (!users.isEmpty()) {
                String userMentions = users.stream()
                        .map(IMentionable::getAsMention).collect(Collectors.joining(lineSeparator()));
                embedBuilder.addField(M_USER_WITH_PERMISSION.tag, userMentions, false);

            }
        }
        messageContext.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void overridePermission(String[] args, MessageEventDataWrapper messageContext, ContextSensitive context) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }

        BooleanState state = ArgumentParser.getBoolean(args[2]);
        if (state == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getTextChannel());
        }

        if (ContextData.setPermissionOverride(context, state.stateAsBoolean, messageContext.getGuild(),
                messageContext)) {
            StringBuilder builder = new StringBuilder();
            if (state.stateAsBoolean) {
                builder.append(TextLocalizer.localizeAllAndReplace(PermissionLocale.M_OVERRIDE_ACTIVATED.tag,
                        messageContext.getGuild(), "**" + context.getContextName() + "**"));
            } else {
                builder.append(TextLocalizer.localizeAllAndReplace(PermissionLocale.M_OVERRIDE_DEACTIVATED.tag,
                        messageContext.getGuild(), "**" + context.getContextName() + "**"));
            }
            builder.append(lineSeparator());
            if (context.needsPermission(messageContext.getGuild())) {
                builder.append(TextLocalizer.localizeAllAndReplace(
                        PermissionLocale.M_PERMISSION_REQUIRED_MESSAGE.tag, messageContext.getGuild()));
            } else {
                builder.append(TextLocalizer.localizeAllAndReplace(
                        PermissionLocale.M_PERMISSION_NOT_REQUIRED_MESSAGE.tag, messageContext.getGuild()));
            }
            MessageSender.sendMessage(builder.toString(), messageContext.getTextChannel());
        }
    }

    private void showMentions(MessageEventDataWrapper messageContext, ContextSensitive context, String message) {
        String roleMentions = ArgumentParser.getRoles(messageContext.getGuild(),
                ContextData.getContextGuildRolePermissions(messageContext.getGuild(),
                        context, messageContext)).stream().map(IMentionable::getAsMention)
                .collect(Collectors.joining(lineSeparator()));
        MessageSender.sendSimpleTextBox(message,
                roleMentions,
                Color.blue, messageContext.getTextChannel());
    }

    private void modifyUsers(String[] args, MessageEventDataWrapper messageContext,
                             ContextSensitive context, ModifyType modifyType) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            return;
        }


        List<User> validUser = ArgumentParser.getGuildUsers(messageContext.getGuild(),
                ArgumentParser.getRangeAsList(Arrays.asList(args), 2));

        for (User user : validUser) {
            if (modifyType == ModifyType.ADD) {
                if (!ContextData.addContextUserPermission(context,
                        messageContext.getGuild(), user, messageContext)) {
                    return;
                }
            } else {
                if (!ContextData.removeContextUserPermission(context,
                        messageContext.getGuild(), user, messageContext)) {
                    return;
                }
            }
        }

        sendMessage(messageContext, context.getContextName(), modifyType,
                M_USER_ACCESS_GRANTED, M_USER_ACCESS_REVOKED, new ArrayList<>(validUser));
    }

    private void modifyRoles(String[] args, MessageEventDataWrapper messageContext,
                             ContextSensitive context, ModifyType modifyType) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            return;
        }

        List<Role> roles = ArgumentParser.getRoles(messageContext.getGuild(), ArgumentParser.getRangeAsList(args, 2));

        for (Role role : roles) {
            if (modifyType == ModifyType.ADD) {
                if (!ContextData.addContextRolePermission(context,
                        messageContext.getGuild(), role, messageContext)) {
                    return;
                }
            } else {
                if (!ContextData.removeContextRolePermission(context,
                        messageContext.getGuild(), role, messageContext)) {
                    return;
                }
            }
        }

        sendMessage(messageContext, context.getContextName(), modifyType,
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
