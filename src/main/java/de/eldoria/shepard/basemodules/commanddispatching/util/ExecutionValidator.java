package de.eldoria.shepard.basemodules.commanddispatching.util;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.GuildChannelOnly;
import de.eldoria.shepard.commandmodules.command.PrivateChannelOnly;
import de.eldoria.shepard.commandmodules.commandsettings.data.CommandData;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
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
     * @param command      command to check
     * @param eventWrapper event wrapper
     * @return true if the user can access this command
     */
    public boolean canAccess(Command command, EventWrapper eventWrapper) {
        if (eventWrapper.isGuildEvent() && command instanceof PrivateChannelOnly) {
            return false;
        }

        if (eventWrapper.isPrivateEvent() && command instanceof GuildChannelOnly) {
            return false;
        }

        if (eventWrapper.isGuildEvent()) {
            return commandData.canAccess(command, eventWrapper.getMember().get());
        } else {
            return commandData.canAccess(command, eventWrapper.getAuthor());
        }

    }

    /**
     * Checks if a user can use a command based on guild permission settings.
     * A user can always execute a command if he is a server administrator
     *
     * @param command command to check
     * @param wrapper wrapper for lookup
     * @return true if the user can use this command
     */
    public boolean canUse(String command, EventWrapper wrapper) {

        if (wrapper.isPrivateEvent()) {
            return true;
        }

        Member member = wrapper.getMember().get();
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
     * @param wrapper wrapper for lookup
     * @return true if the user can use this command
     */
    public boolean canUse(Command command, EventWrapper wrapper) {
        return canUse(command.getCommandIdentifier(), wrapper);
    }

    /**
     * Checks if a user can use a command or subcommand on his guild.
     * These check includes the {@link #canUse(String, EventWrapper)} method.
     *
     * @param command command object
     * @param wrapper wrapper for permission lookup
     * @return true if the command or subcommand can be used in the channel
     */
    public boolean canUseInChannel(Command command, EventWrapper wrapper) {
        return canUseInChannel(command.getCommandIdentifier(), wrapper);
    }

    /**
     * Checks if a user can use a command or subcommand on his guild.
     * These check includes the {@link #canUse(String, EventWrapper)} method.
     *
     * @param command command identifier. A command identifier can be the root
     *                command {@link Command#getCommandIdentifier()} or a string containing the command
     *                and the {@link SubCommand#getSubCommandIdentifier()} separated by a ".".
     * @param wrapper wrapper for permission lookup
     * @return true if the command or subcommand can be used in the channel
     */
    public boolean canUseInChannel(String command, EventWrapper wrapper) {
        if (wrapper.isPrivateEvent()) {
            return true;
        }

        Member member = wrapper.getMember().get();
        Guild guild = wrapper.getGuild().get();
        MessageChannel channel = wrapper.getMessageChannel();

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
     * A command is displayed if the {@link #canUseInChannel(String, EventWrapper)} and
     * {@link #isCommandEnabled(Command, EventWrapper)} return true.
     *
     * @param command command identifier. A command identifier can be the root
     *                command {@link Command#getCommandIdentifier()} or a string containing the command
     *                and the {@link SubCommand#getSubCommandIdentifier()} separated by a ".".
     * @return true if a command should be displayed.
     */
    public boolean displayInHelp(Command command, EventWrapper wrapper) {
        if (wrapper.isGuildEvent() && command instanceof PrivateChannelOnly) {
            return false;
        }

        if (wrapper.isPrivateEvent() && command instanceof GuildChannelOnly) {
            return false;
        }

        if (wrapper.isPrivateEvent()) {
            return canAccess(command, wrapper);
        }

        return commandData.isDisplayedInHelp(command.getCommandIdentifier(), wrapper.getMember().get(),
                wrapper.getGuild().get(), wrapper.getMessageChannel());
    }

    /**
     * Get the state of the command.
     * The state defines if a command is enabled or disabled on a guild.
     * Only Commands can be disabled. Subcommands can not be disabled.
     *
     * @param command command object to check.
     * @param wrapper wrapper for permission lookup
     * @return true if a command is enabled
     */
    public boolean isCommandEnabled(Command command, EventWrapper wrapper) {
        if (wrapper.isPrivateEvent()) {
            return true;
        }

        return commandData.getState(command, wrapper.getGuild().get());
    }

    /**
     * Definies if a command or a subcommand requires a permission.
     * If the command requires a permission all subcommand require a permission too.
     * If a command does not require a permission, a subcommand can require a permission.
     * Wether a command or subcommand require a permission is changed by
     * the {@link CommandData#setPermissionOverride(String, boolean, Guild, EventWrapper)} Method.
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
