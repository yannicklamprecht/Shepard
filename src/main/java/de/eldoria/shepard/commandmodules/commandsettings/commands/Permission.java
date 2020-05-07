package de.eldoria.shepard.commandmodules.commandsettings.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.basemodules.commanddispatching.util.CommandSearchResult;
import de.eldoria.shepard.basemodules.commanddispatching.util.ExecutionValidator;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.commandsettings.data.CommandData;
import de.eldoria.shepard.commandmodules.commandsettings.types.ModifyType;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqExecutionValidator;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_PERMISSION;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_BOOLEAN;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_PERMISSION;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.AD_ROLE_AND_OR_USER;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.A_ROLE_AND_OR_USER;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_ACCESS_LIST;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_GRANT;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_INFO;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_REVOKE;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.C_SET_PERMISSION_OVERRIDE;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_ACCESS_GRANTED;
import static de.eldoria.shepard.localization.enums.commands.admin.PermissionLocale.M_ACCESS_REVOKED;
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
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_INSUFFICIENT_PERMISSION;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static java.lang.System.lineSeparator;

public class Permission extends Command implements Executable, ReqParser, ReqExecutionValidator, ReqDataSource {
    private ArgumentParser parser;
    private CommandData commandData;
    private ExecutionValidator validator;

    /**
     * Creates a new permission command object.
     */
    public Permission() {
        super("permission",
                new String[] {"perm"},
                DESCRIPTION.tag,
                SubCommand.builder("permission")
                        .addSubcommand(C_GRANT.tag,
                                Parameter.createCommand("grant"),
                                Parameter.createInput(A_PERMISSION.tag, AD_PERMISSION.tag, true),
                                Parameter.createInput(A_ROLE_AND_OR_USER.tag, AD_ROLE_AND_OR_USER.tag, true))
                        .addSubcommand(C_REVOKE.tag,
                                Parameter.createCommand("revoke"),
                                Parameter.createInput(A_PERMISSION.tag, AD_PERMISSION.tag, true),
                                Parameter.createInput(A_ROLE_AND_OR_USER.tag, AD_ROLE_AND_OR_USER.tag, true))
                        .addSubcommand(C_ACCESS_LIST.tag,
                                Parameter.createCommand("accessList"),
                                Parameter.createInput(A_PERMISSION.tag, AD_PERMISSION.tag, true))
                        .addSubcommand(C_SET_PERMISSION_OVERRIDE.tag,
                                Parameter.createCommand("setPermissionOverride"),
                                Parameter.createInput(A_PERMISSION.tag, AD_PERMISSION.tag, true),
                                Parameter.createInput(A_BOOLEAN.tag, null, true))
                        .addSubcommand(C_INFO.tag,
                                Parameter.createCommand("info"),
                                Parameter.createInput(A_PERMISSION.tag, AD_PERMISSION.tag, true))
                        .build(),
                CommandCategory.ADMIN);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {

        CommandSearchResult command = parser.searchCommand(args[1]);

        if (command.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.COMMAND_SEARCH_EMPTY,
                    messageContext.getTextChannel());
            return;
        }

        if (!validator.canAccess(command.command().get(), messageContext)) {
            MessageSender.sendSimpleError(ErrorType.COMMAND_SEARCH_EMPTY,
                    messageContext.getTextChannel());
            return;
        }

        if (!validator.canUse(command.getIdentifier(), messageContext.getMember())) {
            MessageSender.sendMessage(localizeAllAndReplace(M_INSUFFICIENT_PERMISSION.tag,
                    messageContext.getGuild(), "**" + command.getIdentifier() + "**"),
                    messageContext.getTextChannel());
            return;
        }

        String cmd = args[0];
        if (isSubCommand(cmd, 0)) {
            modifyPermission(args, messageContext, command.getIdentifier(), ModifyType.ADD);
            return;
        }
        if (isSubCommand(cmd, 1)) {
            modifyPermission(args, messageContext, command.getIdentifier(), ModifyType.REMOVE);
            return;
        }

        if (isSubCommand(cmd, 2)) {
            smallInfo(messageContext, command.getIdentifier());
            return;
        }

        if (isSubCommand(cmd, 3)) {
            overridePermission(args, messageContext, command.getIdentifier());
            return;
        }

        if (isSubCommand(cmd, 4)) {
            info(messageContext, command.command().get());
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
    }


    private void info(MessageEventDataWrapper messageContext, Command command) {
        LocalizedEmbedBuilder embedBuilder = new LocalizedEmbedBuilder(messageContext)
                .setTitle(TextLocalizer.localizeAllAndReplace(M_INFO_TITLE.tag, messageContext.getGuild(),
                        command.getCommandName()));
        Guild guild = messageContext.getGuild();

        // Command
        addCommandInfo(GeneralLocale.A_COMMAND_NAME.tag, false, command.getCommandIdentifier(), guild, embedBuilder);

        // Full Access Permission
        addCommandInfo(PermissionLocale.M_FULL_ACCESS.tag, false, command.getCommandIdentifier()
                + "." + SubCommand.wildcard().getSubCommandIdentifier(), guild, embedBuilder);

        if (command.isStandalone()) {
            // Base Command Permission
            addCommandInfo(PermissionLocale.M_STANDALONE_COMMAND.tag, false, command.getCommandIdentifier()
                    + "." + SubCommand.standalone().getSubCommandIdentifier(), guild, embedBuilder);
        }

        // Sub commands
        for (var subCommand : command.getSubCommands()) {
            addCommandInfo(PermissionLocale.M_SUB_COMMAND + ": " + subCommand.getSubCommandIdentifier(), false,
                    command.getCommandIdentifier() + "." + subCommand.getSubCommandIdentifier(),
                    guild, embedBuilder);
        }

        embedBuilder.addField("", "[" + PermissionLocale.M_CLICK_EXPLANATION + "]"
                        + "(https://gitlab.com/shepardbot/ShepardBot/-/wikis/Commands/Permission-and-Command-Settings)",
                false);

        messageContext.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void addCommandInfo(String title, boolean inline, String permission, Guild guild,
                                LocalizedEmbedBuilder embed) {
        StringBuilder builder = new StringBuilder();

        boolean requirePermission = validator.requirePermission(permission, guild);

        if (!permission.endsWith("*")) {
            builder.append("> *").append(M_PERMISSION_REQUIRED).append("* `").append(requirePermission).append("`\n");
        }
        builder.append("> *").append(GeneralLocale.A_PERMISSION).append("*: `").append(permission).append("`\n");


        if (requirePermission) {
            List<Role> roles = getRolesWithPermissions(permission, guild);
            List<User> users = getUsersWithPermissions(permission, guild);

            if (!roles.isEmpty()) {
                String roleMentions = roles.stream()
                        .map(r -> "> " + r.getAsMention()).collect(Collectors.joining("\n"));
                builder.append(M_ROLES_WITH_PERMISSION.tag).append("\n").append(roleMentions).append("\n");
            }

            if (!users.isEmpty()) {
                String userMentions = users.stream()
                        .map(r -> "> " + r.getAsMention()).collect(Collectors.joining("\n"));
                builder.append(M_USER_WITH_PERMISSION.tag).append("\n").append(userMentions);

            }
        }
        embed.addField(title, builder.toString(), inline);
    }

    private void overridePermission(String[] args, MessageEventDataWrapper messageContext, String command) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }

        BooleanState state = ArgumentParser.getBoolean(args[2]);
        if (state == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getTextChannel());
        }

        if (command.endsWith("*")) {
            MessageSender.sendMessage(PermissionLocale.E_PERMISSION_OVERRIDE.tag,
                    messageContext.getTextChannel());
            return;
        }

        if (!commandData.setPermissionOverride(command, state.stateAsBoolean, messageContext.getGuild(),
                messageContext)) {
            MessageSender.sendSimpleError(ErrorType.DATABASE_ERROR, messageContext.getTextChannel());
        }
        StringBuilder builder = new StringBuilder();
        if (state.stateAsBoolean) {
            builder.append(TextLocalizer.localizeAllAndReplace(PermissionLocale.M_OVERRIDE_ACTIVATED.tag,
                    messageContext.getGuild(), "**" + command + "**"));
        } else {
            builder.append(TextLocalizer.localizeAllAndReplace(PermissionLocale.M_OVERRIDE_DEACTIVATED.tag,
                    messageContext.getGuild(), "**" + command + "**"));
        }
        builder.append(lineSeparator());

        boolean subcommand = command.contains(".");

        if (subcommand) {
            if (validator.requirePermission(command, messageContext.getGuild()) && !state.stateAsBoolean) {
                // Permission override for subcommand is not active because the command requires a permission
                builder.append(localizeAllAndReplace(
                        PermissionLocale.M_SUB_COMMAND_PERMISSION_OVERRIDEN.tag, messageContext.getGuild()));

            } else if (validator.requirePermission(command, messageContext.getGuild())) {
                // Subcommand requires now a permission
                builder.append(TextLocalizer.localizeAllAndReplace(
                        PermissionLocale.M_SUB_COMMAND_PERMISSION_NEEDED.tag, messageContext.getGuild()));
            } else {
                // Subcommand does not require a permission
                builder.append(TextLocalizer.localizeAllAndReplace(
                        PermissionLocale.M_SUB_COMMAND_PERMISSION_NOT_NEEDED.tag, messageContext.getGuild()));
            }
        } else {
            if (validator.requirePermission(command, messageContext.getGuild())) {
                builder.append(TextLocalizer.localizeAllAndReplace(
                        PermissionLocale.M_PERMISSION_REQUIRED_MESSAGE.tag, messageContext.getGuild()));
            } else {
                builder.append(TextLocalizer.localizeAllAndReplace(
                        PermissionLocale.M_PERMISSION_NOT_REQUIRED_MESSAGE.tag, messageContext.getGuild()));
            }
        }
        MessageSender.sendMessage(builder.toString(), messageContext.getTextChannel());

    }

    private void smallInfo(MessageEventDataWrapper messageContext, String command) {
        LocalizedEmbedBuilder embedBuilder = new LocalizedEmbedBuilder(messageContext)
                .setTitle(TextLocalizer.localizeAllAndReplace(M_INFO_TITLE.tag, messageContext.getGuild(), command));

        List<User> users = getUsersWithPermissions(command, messageContext.getGuild());
        List<Role> roles = getRolesWithPermissions(command, messageContext.getGuild());

        String mentions;
        if (!users.isEmpty()) {
            mentions = users.stream().map(IMentionable::getAsMention)
                    .collect(Collectors.joining(lineSeparator()));
            embedBuilder.addField(M_USER_ACCESS.tag, mentions, false);
        }

        if (!roles.isEmpty()) {
            mentions = roles.stream().map(IMentionable::getAsMention)
                    .collect(Collectors.joining(lineSeparator()));
            embedBuilder.addField(M_ROLE_ACCESS.tag, mentions, false);
        }

        messageContext.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void modifyPermission(String[] args, MessageEventDataWrapper messageContext, String context,
                                  ModifyType modifyType) {
        List<User> validUser = parser.getGuildUsers(messageContext.getGuild(),
                ArgumentParser.getRangeAsList(Arrays.asList(args), 2));

        List<Role> validRoles = parser.getRoles(messageContext.getGuild(),
                ArgumentParser.getRangeAsList(args, 2));

        for (User user : validUser) {
            if (modifyType == ModifyType.ADD) {
                if (!commandData.addUserPermission(context,
                        messageContext.getGuild(), user, messageContext)) {
                    return;
                }
            } else {
                if (!commandData.removeUserPermission(context,
                        messageContext.getGuild(), user, messageContext)) {
                    return;
                }
            }
        }

        for (Role role : validRoles) {
            if (modifyType == ModifyType.ADD) {
                if (!commandData.addRolePermission(context,
                        messageContext.getGuild(), role, messageContext)) {
                    return;
                }
            } else {
                if (!commandData.removeRolePermission(context,
                        messageContext.getGuild(), role, messageContext)) {
                    return;
                }
            }
        }

        sendMessage(messageContext, context, modifyType, validUser, validRoles);

    }

    private void sendMessage(MessageEventDataWrapper messageContext, String contextName, ModifyType modifyType,
                             List<User> users, List<Role> roles) {
        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(messageContext)
                .setColor(modifyType == ModifyType.ADD ? Colors.Pastel.DARK_GREEN : Colors.Pastel.RED)
                .setTitle(localizeAllAndReplace(modifyType == ModifyType.ADD
                        ? M_ACCESS_GRANTED.tag : M_ACCESS_REVOKED.tag, messageContext.getGuild(), contextName));

        if (!users.isEmpty()) {
            String names = users.stream().map(IMentionable::getAsMention).collect(Collectors.joining(lineSeparator()));
            builder.addField(modifyType == ModifyType.ADD
                    ? M_USER_ACCESS_GRANTED.tag : M_USER_ACCESS_REVOKED.tag, names, false);
        }
        if (!roles.isEmpty()) {
            String names = roles.stream().map(IMentionable::getAsMention).collect(Collectors.joining(lineSeparator()));
            builder.addField(modifyType == ModifyType.ADD
                    ? M_ROLE_ACCESS_GRANTED.tag : M_ROLE_ACCESS_REVOKED.tag, names, false);
        }

        messageContext.getTextChannel().sendMessage(builder.build()).queue();
    }

    /**
     * Get the roles, which have access to this context on the
     * guild of the {@link MessageEventDataWrapper} message context.
     *
     * @param guild guild lookup
     * @return list of all roles with access to this context
     */
    private List<Role> getRolesWithPermissions(String command, Guild guild) {
        return parser.getRoles(guild,
                commandData.getRolePermissionList(command, guild));
    }

    /**
     * Get the users, which have access to this context on the
     * guild of the {@link MessageEventDataWrapper} message context.
     *
     * @param guild guild lookup
     * @return list of all roles with access to this context
     */
    private List<User> getUsersWithPermissions(String command, Guild guild) {
        return parser.getGuildUsers(guild,
                commandData.getUserPermissionList(command, guild));
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }

    @Override
    public void addDataSource(DataSource source) {
        commandData = new CommandData(source);
    }

    @Override
    public void addExecutionValidator(ExecutionValidator validator) {
        this.validator = validator;
    }
}
