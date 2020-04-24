package de.eldoria.shepard.commandmodules.commandsettings.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.commandsettings.data.CommandData;
import de.eldoria.shepard.commandmodules.commandsettings.types.ModifyType;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import javax.sql.DataSource;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_CONTEXT_NAME;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_ROLES;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_USERS;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_BOOLEAN;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_CONTEXT_NAME;
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
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_GENERAL_INFORMATION;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_INFO_TITLE;
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

public class Permission extends Command implements Executable, ReqParser, ReqDataSource {
    private ArgumentParser parser;
    private CommandData commandData;

    /**
     * Creates a new permission command object.
     */
    public Permission() {
        super("permission",
                new String[] {"perm"},
                DESCRIPTION.tag,
                SubCommand.builder("permission")
                        .addSubcommand(C_ADD_USER.tag,
                                Parameter.createCommand("addUser"),
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true),
                                Parameter.createInput(A_USERS.tag, AD_USERS.tag, true))
                        .addSubcommand(C_REMOVE_USER.tag,
                                Parameter.createCommand("removeUser"),
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true),
                                Parameter.createInput(A_USERS.tag, AD_USERS.tag, true))
                        .addSubcommand(C_LIST_USER.tag,
                                Parameter.createCommand("listUser"),
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true))
                        .addSubcommand(C_ADD_ROLE.tag,
                                Parameter.createCommand("addRole"),
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true),
                                Parameter.createInput(A_ROLES.tag, AD_ROLES.tag, true))
                        .addSubcommand(C_REMOVE_ROLE.tag,
                                Parameter.createCommand("removeRole"),
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true),
                                Parameter.createInput(A_ROLES.tag, AD_ROLES.tag, true))
                        .addSubcommand(C_LIST_ROLE.tag,
                                Parameter.createCommand("listRoles"),
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true))
                        .addSubcommand(C_SET_PERMISSION_OVERRIDE.tag,
                                Parameter.createCommand("setPermissionOverride"),
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true),
                                Parameter.createInput(A_BOOLEAN.tag, null, true))
                        .addSubcommand(C_INFO.tag,
                                Parameter.createCommand("info"),
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true))
                        .build(),
                CommandCategory.ADMIN);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];

        Optional<Command> command = parser.getCommand(args[1], messageContext);

        if (command.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND,
                    messageContext.getTextChannel());
            return;
        }

        if (isSubCommand(cmd, 0)) {
            modifyUsers(args, messageContext, command.get(), ModifyType.ADD);
            return;
        }
        if (isSubCommand(cmd, 1)) {
            modifyUsers(args, messageContext, command.get(), ModifyType.REMOVE);
            return;
        }

        if (isSubCommand(cmd, 2)) {
            showMentions(messageContext, command.get(), M_USER_ACCESS.tag);
            return;
        }

        if (isSubCommand(cmd, 3)) {
            modifyRoles(args, messageContext, command.get(), ModifyType.ADD);
            return;
        }
        if (isSubCommand(cmd, 4)) {
            modifyRoles(args, messageContext, command.get(), ModifyType.REMOVE);
            return;
        }

        if (isSubCommand(cmd, 5)) {
            showMentions(messageContext, command.get(), M_ROLE_ACCESS.tag);
            return;
        }

        if (isSubCommand(cmd, 6)) {
            overridePermission(args, messageContext, command.get());
            return;
        }

        if (isSubCommand(cmd, 7)) {
            info(messageContext, command.get());
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
    }

    private void info(MessageEventDataWrapper messageContext, Command command) {
        LocalizedEmbedBuilder embedBuilder = new LocalizedEmbedBuilder(messageContext)
                .setTitle(TextLocalizer.localizeAllAndReplace(M_INFO_TITLE.tag, messageContext.getGuild(),
                        command.getCommandName()));

        boolean requirePermission = commandData.requirePermission(command, messageContext.getGuild());

        String builder = M_PERMISSION_REQUIRED + " " + (requirePermission);
        embedBuilder.addField(M_GENERAL_INFORMATION.tag, builder, false);

        if (requirePermission) {
            List<Role> roles = getRolesWithPermissions(command, messageContext);
            List<User> users = getUsersWithPermissions(command, messageContext);

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

    private void overridePermission(String[] args, MessageEventDataWrapper messageContext, Command command) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }

        BooleanState state = ArgumentParser.getBoolean(args[2]);
        if (state == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getTextChannel());
        }

        if (commandData.setPermissionOverride(command, state.stateAsBoolean, messageContext.getGuild(),
                messageContext)) {
            StringBuilder builder = new StringBuilder();
            if (state.stateAsBoolean) {
                builder.append(TextLocalizer.localizeAllAndReplace(PermissionLocale.M_OVERRIDE_ACTIVATED.tag,
                        messageContext.getGuild(), "**" + command.getCommandName() + "**"));
            } else {
                builder.append(TextLocalizer.localizeAllAndReplace(PermissionLocale.M_OVERRIDE_DEACTIVATED.tag,
                        messageContext.getGuild(), "**" + command.getCommandName() + "**"));
            }
            builder.append(lineSeparator());
            if (commandData.requirePermission(command, messageContext.getGuild())) {
                builder.append(TextLocalizer.localizeAllAndReplace(
                        PermissionLocale.M_PERMISSION_REQUIRED_MESSAGE.tag, messageContext.getGuild()));
            } else {
                builder.append(TextLocalizer.localizeAllAndReplace(
                        PermissionLocale.M_PERMISSION_NOT_REQUIRED_MESSAGE.tag, messageContext.getGuild()));
            }
            MessageSender.sendMessage(builder.toString(), messageContext.getTextChannel());
        }
    }

    private void showMentions(MessageEventDataWrapper messageContext, Command command, String message) {
        String mentions;
        if (message.equalsIgnoreCase(M_USER_ACCESS.tag)) {
            mentions = parser.getGuildUsers(messageContext.getGuild(),
                    commandData.getUserPermissionList(command, messageContext.getGuild()))
                    .stream().map(IMentionable::getAsMention)
                    .collect(Collectors.joining(lineSeparator()));
        } else {
            mentions = parser.getRoles(messageContext.getGuild(),
                    commandData.getRolePermissionList(command, messageContext.getGuild()))
                    .stream().map(IMentionable::getAsMention)
                    .collect(Collectors.joining(lineSeparator()));
        }
        MessageSender.sendSimpleTextBox(message,
                mentions,
                Color.blue, messageContext.getTextChannel());
    }

    private void modifyUsers(String[] args, MessageEventDataWrapper messageContext,
                             Command context, ModifyType modifyType) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            return;
        }


        List<User> validUser = parser.getGuildUsers(messageContext.getGuild(),
                ArgumentParser.getRangeAsList(Arrays.asList(args), 2));

        for (User user : validUser) {
            if (modifyType == ModifyType.ADD) {
                if (!commandData.addContextUserPermission(context,
                        messageContext.getGuild(), user, messageContext)) {
                    return;
                }
            } else {
                if (!commandData.removeContextUserPermission(context,
                        messageContext.getGuild(), user, messageContext)) {
                    return;
                }
            }
        }

        sendMessage(messageContext, context.getCommandName(), modifyType,
                M_USER_ACCESS_GRANTED, M_USER_ACCESS_REVOKED, new ArrayList<>(validUser));
    }

    private void modifyRoles(String[] args, MessageEventDataWrapper messageContext,
                             Command context, ModifyType modifyType) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            return;
        }

        List<Role> roles = parser.getRoles(messageContext.getGuild(), ArgumentParser.getRangeAsList(args, 2));

        for (Role role : roles) {
            if (modifyType == ModifyType.ADD) {
                if (!commandData.addContextRolePermission(context,
                        messageContext.getGuild(), role, messageContext)) {
                    return;
                }
            } else {
                if (!commandData.removeContextRolePermission(context,
                        messageContext.getGuild(), role, messageContext)) {
                    return;
                }
            }
        }

        sendMessage(messageContext, context.getCommandName(), modifyType,
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

    /**
     * Get the roles, which have access to this context on the
     * guild of the {@link MessageEventDataWrapper} message context.
     *
     * @param messageContext message context for guild lookup
     * @return list of all roles with access to this context
     */
    private List<Role> getRolesWithPermissions(Command command, MessageEventDataWrapper messageContext) {
        return parser.getRoles(messageContext.getGuild(),
                commandData.getRolePermissionList(command, messageContext.getGuild()));
    }

    /**
     * Get the users, which have access to this context on the
     * guild of the {@link MessageEventDataWrapper} message context.
     *
     * @param messageContext message context for guild lookup
     * @return list of all roles with access to this context
     */
    private List<User> getUsersWithPermissions(Command command, MessageEventDataWrapper messageContext) {
        return parser.getGuildUsers(messageContext.getGuild(),
                commandData.getUserPermissionList(command, messageContext.getGuild()));
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }

    @Override
    public void addDataSource(DataSource source) {
        commandData = new CommandData(source);
    }
}
