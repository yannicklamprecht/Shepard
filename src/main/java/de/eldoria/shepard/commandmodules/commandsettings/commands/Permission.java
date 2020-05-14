package de.eldoria.shepard.commandmodules.commandsettings.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.basemodules.commanddispatching.util.CommandSearchResult;
import de.eldoria.shepard.basemodules.commanddispatching.util.ExecutionValidator;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
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
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
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

@CommandUsage(EventContext.GUILD)
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
    public void execute(String label, String[] args, EventWrapper wrapper) {

        CommandSearchResult command = parser.searchCommand(args[1]);

        if (command.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.COMMAND_SEARCH_EMPTY,
                    wrapper);
            return;
        }

        if (!validator.canAccess(command.command().get(), wrapper)) {
            MessageSender.sendSimpleError(ErrorType.COMMAND_SEARCH_EMPTY,
                    wrapper);
            return;
        }

        if (!validator.canUse(command.getIdentifier(), wrapper)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_INSUFFICIENT_PERMISSION.tag,
                    wrapper, "**" + command.getIdentifier() + "**"),
                    wrapper.getMessageChannel());
            return;
        }

        String cmd = args[0];
        if (isSubCommand(cmd, 0)) {
            modifyPermission(args, wrapper, command.getIdentifier(), ModifyType.ADD);
            return;
        }
        if (isSubCommand(cmd, 1)) {
            modifyPermission(args, wrapper, command.getIdentifier(), ModifyType.REMOVE);
            return;
        }

        if (isSubCommand(cmd, 2)) {
            smallInfo(wrapper, command.getIdentifier());
            return;
        }

        if (isSubCommand(cmd, 3)) {
            overridePermission(args, wrapper, command.getIdentifier());
            return;
        }

        if (isSubCommand(cmd, 4)) {
            info(wrapper, command.command().get());
            return;
        }
    }


    private void info(EventWrapper wrapper, Command command) {
        LocalizedEmbedBuilder embedBuilder = new LocalizedEmbedBuilder(wrapper)
                .setTitle(TextLocalizer.localizeAllAndReplace(M_INFO_TITLE.tag, wrapper,
                        command.getCommandName()));
        Guild guild = wrapper.getGuild().get();

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

        wrapper.getMessageChannel().sendMessage(embedBuilder.build()).queue();
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

    private void overridePermission(String[] args, EventWrapper wrapper, String command) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, wrapper);
            return;
        }

        BooleanState state = ArgumentParser.getBoolean(args[2]);
        if (state == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, wrapper);
        }

        if (command.endsWith("*")) {
            MessageSender.sendMessage(PermissionLocale.E_PERMISSION_OVERRIDE.tag,
                    wrapper.getMessageChannel());
            return;
        }

        if (!commandData.setPermissionOverride(command, state.stateAsBoolean, wrapper.getGuild().get(),
                wrapper)) {
            MessageSender.sendSimpleError(ErrorType.DATABASE_ERROR, wrapper);
        }
        StringBuilder builder = new StringBuilder();
        if (state.stateAsBoolean) {
            builder.append(TextLocalizer.localizeAllAndReplace(PermissionLocale.M_OVERRIDE_ACTIVATED.tag,
                    wrapper, "**" + command + "**"));
        } else {
            builder.append(TextLocalizer.localizeAllAndReplace(PermissionLocale.M_OVERRIDE_DEACTIVATED.tag,
                    wrapper, "**" + command + "**"));
        }
        builder.append(lineSeparator());

        boolean subcommand = command.contains(".");

        if (subcommand) {
            if (validator.requirePermission(command, wrapper.getGuild().get()) && !state.stateAsBoolean) {
                // Permission override for subcommand is not active because the command requires a permission
                builder.append(localizeAllAndReplace(
                        PermissionLocale.M_SUB_COMMAND_PERMISSION_OVERRIDEN.tag, wrapper));

            } else if (validator.requirePermission(command, wrapper.getGuild().get())) {
                // Subcommand requires now a permission
                builder.append(TextLocalizer.localizeAllAndReplace(
                        PermissionLocale.M_SUB_COMMAND_PERMISSION_NEEDED.tag, wrapper));
            } else {
                // Subcommand does not require a permission
                builder.append(TextLocalizer.localizeAllAndReplace(
                        PermissionLocale.M_SUB_COMMAND_PERMISSION_NOT_NEEDED.tag, wrapper));
            }
        } else {
            if (validator.requirePermission(command, wrapper.getGuild().get())) {
                builder.append(TextLocalizer.localizeAllAndReplace(
                        PermissionLocale.M_PERMISSION_REQUIRED_MESSAGE.tag, wrapper));
            } else {
                builder.append(TextLocalizer.localizeAllAndReplace(
                        PermissionLocale.M_PERMISSION_NOT_REQUIRED_MESSAGE.tag, wrapper));
            }
        }
        MessageSender.sendMessage(builder.toString(), wrapper.getMessageChannel());

    }

    private void smallInfo(EventWrapper wrapper, String command) {
        LocalizedEmbedBuilder embedBuilder = new LocalizedEmbedBuilder(wrapper)
                .setTitle(TextLocalizer.localizeAllAndReplace(M_INFO_TITLE.tag, wrapper, command));

        List<User> users = getUsersWithPermissions(command, wrapper.getGuild().get());
        List<Role> roles = getRolesWithPermissions(command, wrapper.getGuild().get());

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

        wrapper.getMessageChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void modifyPermission(String[] args, EventWrapper wrapper, String context,
                                  ModifyType modifyType) {
        List<User> validUser = parser.getGuildUsers(wrapper.getGuild().get(),
                ArgumentParser.getRangeAsList(Arrays.asList(args), 2));

        List<Role> validRoles = parser.getRoles(wrapper.getGuild().get(),
                ArgumentParser.getRangeAsList(args, 2));

        for (User user : validUser) {
            if (modifyType == ModifyType.ADD) {
                if (!commandData.addUserPermission(context,
                        wrapper.getGuild().get(), user, wrapper)) {
                    return;
                }
            } else {
                if (!commandData.removeUserPermission(context,
                        wrapper.getGuild().get(), user, wrapper)) {
                    return;
                }
            }
        }

        for (Role role : validRoles) {
            if (modifyType == ModifyType.ADD) {
                if (!commandData.addRolePermission(context,
                        wrapper.getGuild().get(), role, wrapper)) {
                    return;
                }
            } else {
                if (!commandData.removeRolePermission(context,
                        wrapper.getGuild().get(), role, wrapper)) {
                    return;
                }
            }
        }

        sendMessage(wrapper, context, modifyType, validUser, validRoles);

    }

    private void sendMessage(EventWrapper wrapper, String contextName, ModifyType modifyType,
                             List<User> users, List<Role> roles) {
        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(wrapper)
                .setColor(modifyType == ModifyType.ADD ? Colors.Pastel.DARK_GREEN : Colors.Pastel.RED)
                .setTitle(localizeAllAndReplace(modifyType == ModifyType.ADD
                        ? M_ACCESS_GRANTED.tag : M_ACCESS_REVOKED.tag, wrapper, contextName));

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

        wrapper.getMessageChannel().sendMessage(builder.build()).queue();
    }

    /**
     * Get the roles, which have access to this context on the guild.
     *
     * @param guild guild lookup
     * @return list of all roles with access to this context
     */
    private List<Role> getRolesWithPermissions(String command, Guild guild) {
        return parser.getRoles(guild,
                commandData.getRolePermissionList(command, guild));
    }

    /**
     * Get the users, which have access to this context on the guild.
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
