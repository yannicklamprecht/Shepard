package de.eldoria.shepard.basemodules.commanddispatching.util;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.commandsettings.data.CommandData;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.sql.DataSource;

public class ExecutionValidator implements ReqDataSource {

    private CommandData commandData;

    /**
     * Create a new execution validator.
     */
    public ExecutionValidator() {
    }

    /**
     * Checks if a user can access a command globally.
     *
     * @param command        command to check
     * @param messageContext event wrapper
     * @return true if the user can access this command
     */
    public boolean canAccess(Command command, MessageEventDataWrapper messageContext) {
        return commandData.canAccess(command, messageContext.getMember());
    }

    /**
     * Checks if a user can use a command based on guild permission settings.
     * A user can always execute a command if he is a server administrator
     *
     * @param command command to check
     * @param member  member to check
     * @return true if the user can use this command
     */
    public boolean canUse(String command, Member member) {
        //Checks if a command is not admin only and override is inactive or if the member is a administrator
        if ((member != null && member.hasPermission(Permission.ADMINISTRATOR))) {
            return true;
        }
        if (member == null) {
            return false;
        }

        if (!commandData.canUse(command, member)) {
            return commandData.canUse(command.split("\\.")[0] + ".*", member);
        }
        return true;
    }

    /**
     * Checks if a user can use a command based on guild permission settings.
     * A user can always execute a command if he is a server administrator
     *
     * @param command command to check
     * @param member  member to check
     * @return true if the user can use this command
     */
    public boolean canUse(Command command, Member member) {
        return canUse(command.getCommandIdentifier(), member);
    }

    /**
     * Checks if a user can use a command or subcommand on his guild.
     * These check includes the {@link #canUse(String, Member)} method.
     *
     * @param command command object
     * @param member  member to check
     * @param guild   guild to check
     * @param channel channel to check
     * @return true if the command or subcommand can be used in the channel
     */
    public boolean canUseInChannel(Command command, Member member, Guild guild, TextChannel channel) {
        return canUseInChannel(command.getCommandIdentifier(), member, guild, channel);
    }

    /**
     * Checks if a user can use a command or subcommand on his guild.
     * These check includes the {@link #canUse(String, Member)} method.
     *
     * @param command command identifier. A command identifier can be the root
     *                command {@link Command#getCommandIdentifier()} or a string containing the command
     *                and the {@link SubCommand#getSubCommandIdentifier()} separated by a ".".
     * @param member  member to check
     * @param guild   guild to check
     * @param channel channel to check
     * @return true if the command or subcommand can be used in the channel
     */
    public boolean canUseInChannel(String command, Member member, Guild guild, TextChannel channel) {
        //Checks if a command is not admin only and override is inactive or if the member is a administrator
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            return true;
        }

        if (!commandData.canUseInChannel(command, member, guild, channel)) {
            return commandData.canUseInChannel(command.split("\\.")[0] + ".*", member, guild, channel);
        }
        return true;
    }

    /**
     * Checks if a command should be displayed in help.
     * A command is displayed if the {@link #canUseInChannel(String, Member, Guild, TextChannel)} and
     * {@link #isCommandEnabled(Command, Guild)} return true.
     *
     * @param command command identifier. A command identifier can be the root
     *                command {@link Command#getCommandIdentifier()} or a string containing the command
     *                and the {@link SubCommand#getSubCommandIdentifier()} separated by a ".".
     * @param member  member to check for
     * @param guild   guild to check for
     * @param channel channel to check for
     * @return true if a command should be displayed.
     */
    public boolean displayInHelp(Command command, Member member, Guild guild, TextChannel channel) {
        return commandData.isDisplayedInHelp(command.getCommandIdentifier(), member, guild, channel);
    }

    /**
     * Get the state of the command.
     * The state defines if a command is enabled or disabled on a guild.
     * Only Commands can be disabled. Subcommands can not be disabled.
     *
     * @param command command object to check.
     * @param guild   guild to check
     * @return true if a command is enabled
     */
    public boolean isCommandEnabled(Command command, Guild guild) {
        return commandData.getState(command, guild);
    }

    /**
     * Definies if a command or a subcommand requires a permission.
     * If the command requires a permission all subcommand require a permission too.
     * If a command does not require a permission, a subcommand can require a permission.
     * Wether a command or subcommand require a permission is changed by
     * the {@link CommandData#setPermissionOverride(String, boolean, Guild, MessageEventDataWrapper)} Method.
     *
     * @param command command identifier. A command identifier can be the root
     *                command {@link Command#getCommandIdentifier()} or a string containing the command
     *                and the {@link SubCommand#getSubCommandIdentifier()} separated by a ".".
     * @param guild   the guild to check.
     * @return true if a command requires a permission.
     */
    public boolean requirePermission(String command, Guild guild) {
        return commandData.requiresPermission(command, guild);
    }

    @Override
    public void addDataSource(DataSource source) {
        commandData = new CommandData(source);
    }
}
