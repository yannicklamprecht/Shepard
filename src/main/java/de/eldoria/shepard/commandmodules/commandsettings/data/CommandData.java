package de.eldoria.shepard.commandmodules.commandsettings.data;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.commandsettings.types.CommandSettings;
import de.eldoria.shepard.commandmodules.commandsettings.types.ListType;
import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.eldoria.shepard.database.DbUtil.getSnowflakeArray;
import static de.eldoria.shepard.database.DbUtil.handleException;

public final class CommandData extends QueryObject {
    /**
     * Create a new command data object.
     *
     * @param source for connection handling.
     */
    public CommandData(DataSource source) {
        super(source);
    }

    /**
     * Adds a user to a context list.
     *
     * @param context        context to change
     * @param user           user to add
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean addUser(Command context, User user, EventWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.add_command_user(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setString(2, user.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Removes a user from the context list.
     *
     * @param context        context to change
     * @param user           user to remove
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean removeUser(Command context, User user,
                              EventWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.remove_command_user(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setString(2, user.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Adds a guild to the context list.
     *
     * @param context        context to change
     * @param guild          guild id to add
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean addGuild(Command context, Guild guild,
                            EventWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.add_command_guild(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setString(2, guild.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Removes a guild from the command list.
     *
     * @param command        command to change
     * @param guild          guild id to remove
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean removeGuild(Command command, Guild guild,
                               EventWrapper messageContext) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.remove_command_guild(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setString(2, guild.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Adds a command user permission on a guild.
     *
     * @param command        command identifier. A command identifier can be the root
     *                       command {@link Command#getCommandIdentifier()} or a string containing the command
     *                       and the {@link SubCommand#getSubCommandIdentifier()} separated by a ".".
     * @param guild          guild id where the permission should be added
     * @param user           user which should be added
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean addUserPermission(String command, Guild guild,
                                     User user, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.add_command_user_permission(?,?,?)")) {
            statement.setString(1, command);
            statement.setString(2, guild.getId());
            statement.setString(3, user.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Removes a command user permission on a guild.
     *
     * @param command        command identifier. A command identifier can be the root
     *                       command {@link Command#getCommandIdentifier()} or a string containing the command
     *                       and the {@link SubCommand#getSubCommandIdentifier()} separated by a ".".
     * @param guild          guild id where the permission should be removed
     * @param user           user which should be removed
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean removeUserPermission(String command, Guild guild,
                                        User user, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.remove_command_user_permission(?,?,?)")) {
            statement.setString(1, command);
            statement.setString(2, guild.getId());
            statement.setString(3, user.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Adds a command role permission on a guild.
     *
     * @param command        command identifier. A command identifier can be the root
     *                       command {@link Command#getCommandIdentifier()} or a string containing the command
     *                       and the {@link SubCommand#getSubCommandIdentifier()} separated by a ".".
     * @param guild          guild id where the permission should be added
     * @param role           role which should be added
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean addRolePermission(String command, Guild guild,
                                     Role role, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.add_command_role_permission(?,?,?)")) {
            statement.setString(1, command);
            statement.setString(2, guild.getId());
            statement.setString(3, role.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Removes a command role permission on a guild.
     *
     * @param command        command to change
     * @param guild          guild id where the permission should be removed
     * @param role           role which should be removed
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean removeRolePermission(String command, Guild guild,
                                        Role role, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.remove_command_role_permission(?,?,?)")) {
            statement.setString(1, command);
            statement.setString(2, guild.getId());
            statement.setString(3, role.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Changes the context admin only state.
     *
     * @param context        context to change
     * @param state          True if it is a admin only command.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setAdmin(Command context, boolean state,
                            EventWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.set_command_admin(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Changes the context nsfw state.
     *
     * @param context        context to change
     * @param state          True if it is a nsfw command.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setNsfw(Command context, boolean state,
                           EventWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.set_command_nsfw(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Activates or deactivates the user check for this context.
     *
     * @param context        context to change
     * @param state          true when user should be checked
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setUserCheckActive(Command context, boolean state,
                                      EventWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.set_command_user_check_active(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Activates or deactivates the guild check for this context.
     *
     * @param context        context to change
     * @param state          true when guild should be checked
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setGuildCheckActive(Command context, boolean state,
                                       EventWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.set_command_guild_check_active(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Changes the context user list type.
     *
     * @param context        context to change
     * @param listType       ListType enum.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setUserListType(Command context, ListType listType,
                                   EventWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.set_command_user_list_type(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setString(2, listType.toString());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Changes the context guild list type.
     *
     * @param context        context to change
     * @param listType       ListType enum.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setGuildListType(Command context, ListType listType,
                                    EventWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.set_command_guild_list_type(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setString(2, listType.toString());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Set the global guild cooldown to a context.
     *
     * @param context        context to change
     * @param seconds        cooldown in seconds
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setGuildCooldown(Command context, int seconds,
                                    EventWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.set_guild_cooldown(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setInt(2, seconds);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Set the global user cooldown to a context.
     *
     * @param context        context to change
     * @param seconds        cooldown in seconds
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setUserCooldown(Command context, int seconds,
                                   EventWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.set_user_cooldown(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setInt(2, seconds);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Sets a permission override on the {@link Guild} for the
     * specific command or subcommand.
     *
     * @param command        command identifier. A command identifier can be the root
     *                       command {@link Command#getCommandIdentifier()} or a string containing the command
     *                       and the {@link SubCommand#getSubCommandIdentifier()} separated by a ".".
     * @param state          state
     * @param guild          guild to add
     * @param messageContext message command for error handling
     * @return true if the database access was successful
     */
    public boolean setPermissionOverride(String command, boolean state,
                                         Guild guild, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.set_permission_override(?,?,?)")) {
            statement.setString(1, command);
            statement.setString(2, guild.getId());
            statement.setBoolean(3, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Returns the context data of the needed context. Expensive! Dont use for permission check.
     *
     * @param context        context to change
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return Context data object.
     */
    public CommandSettings getCommandData(Command context, EventWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        CommandSettings data = new CommandSettings();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_settings.get_command_data(?)")) {
            statement.setString(1, commandIdentifier);
            ResultSet result = statement.executeQuery();

            if (result.next()) {

                data.setAdminOnly(result.getBoolean("admin_only"));
                data.setNsfw(result.getBoolean("nsfw"));
                data.setUserCheckActive(result.getBoolean("user_check_state"));
                data.setUserListType(ListType.getType(result.getString("user_list_type")));
                if (result.getArray("user_list") == null) {
                    data.setUserList(new String[0]);
                } else {
                    data.setUserList((String[]) result.getArray("user_list").getArray());
                }
                data.setGuildCheckActive(result.getBoolean("guild_check_state"));
                data.setGuildListType(ListType.getType(result.getString("guild_list_type")));
                if (result.getArray("guild_list") == null) {
                    data.setGuildList(new String[0]);
                } else {
                    data.setGuildList((String[]) result.getArray("guild_list").getArray());
                }
                data.setUserCooldown(result.getInt("user_cooldown"));
                data.setGuildCooldown(result.getInt("guild_cooldown"));
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
            return data;
        }

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_settings.get_permission_overrides(?)")) {
            statement.setString(1, commandIdentifier);
            ResultSet result = statement.executeQuery();

            Map<Long, Boolean> overrides = new HashMap<>();
            while (result.next()) {
                overrides.put(
                        Long.parseLong(result.getString("guild_id")),
                        result.getBoolean("override")
                );
            }
            data.setPermissionOverride(overrides);
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return data;
    }

    // Access Validation

    /**
     * Get the user cooldown of a command.
     *
     * @param command command to get the cooldown
     * @return cooldown as integer. May be 0, but not negative;
     */
    public int getUserCooldown(Command command) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.get_user_cooldown(?)")) {
            statement.setString(1, commandIdentifier);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int cooldown = resultSet.getInt(1);
                if (cooldown < 0) {
                    throw new IllegalStateException("User cooldown of command " + commandIdentifier + " is negative");
                }
                return cooldown;
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    /**
     * Get the guild cooldown of a command.
     *
     * @param command command to get the cooldown
     * @return cooldown as integer. May be 0, but not negative;
     */
    public int getGuildCooldown(Command command) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.get_guild_cooldown(?)")) {
            statement.setString(1, commandIdentifier);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int cooldown = resultSet.getInt(1);
                if (cooldown < 0) {
                    throw new IllegalStateException("Guild cooldown of command " + commandIdentifier + " is negative");
                }
                return cooldown;
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    // Server settings

    /**
     * Retrieve a list of all users which have the permission to access this command or subcommand.
     *
     * @param command command identifier. A command identifier can be the root
     *                command {@link Command#getCommandIdentifier()} or a string containing the command
     *                and the {@link SubCommand#getSubCommandIdentifier()} separated by a ".".
     * @param guild   guild to check in
     * @return List of user ids. Can be empty.
     */
    public List<String> getUserPermissionList(String command, Guild guild) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.get_user_permission_list(?,?)")) {
            statement.setString(1, command);
            statement.setLong(2, guild.getIdLong());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Array array = resultSet.getArray(1);
                if (array == null) {
                    return Collections.emptyList();
                }
                Long[] idArray = (Long[]) array.getArray();
                return Arrays.stream(idArray).map(String::valueOf).collect(Collectors.toList());
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(command + " has no complete setup");
    }

    /**
     * Retrieve a list of all roles which have the permission to access this command or subcommand.
     *
     * @param command command identifier. A command identifier can be the root
     *                command {@link Command#getCommandIdentifier()} or a string containing the command
     *                and the {@link SubCommand#getSubCommandIdentifier()} separated by a ".".
     * @param guild   guild to check in
     * @return List of role ids. Can be empty.
     */
    public List<String> getRolePermissionList(String command, Guild guild) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.get_role_permission_list(?,?)")) {
            statement.setString(1, command);
            statement.setLong(2, guild.getIdLong());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getArray(1) == null) {
                    return Collections.emptyList();
                }
                return Arrays.stream((Long[]) resultSet.getArray(1).getArray())
                        .map(String::valueOf).collect(Collectors.toList());
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(command + " has no complete setup");
    }

    /**
     * Checks if a user can generally use a command or subcommand.
     * This will be true if a user has the permission or a role with the permission to use this command.
     * The result is only determined by guild settings.
     * See {@link #canAccess(Command, Member)} for a check of the bot settings.
     *
     * @param command command identifier. A command identifier can be the root
     *                command {@link Command#getCommandIdentifier()} or a string containing the command
     *                and the {@link SubCommand#getSubCommandIdentifier()} separated by a ".".
     * @param member  member to check
     * @return true if the user can use the command.
     */
    public boolean canUse(String command, Member member) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.can_use(?,?,?,?)")) {
            statement.setString(1, command);
            statement.setLong(2, member.getGuild().getIdLong());
            statement.setLong(3, member.getIdLong());
            Long[] roles = new Long[member.getRoles().size()];
            Array roleIds = conn.createArrayOf("bigint", member.getRoles()
                    .stream().map(ISnowflake::getIdLong).collect(Collectors.toList()).toArray(roles));
            statement.setArray(4, roleIds);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(command + " has no complete setup");
    }



    /**
     * Checks if a user or guild can access a command.
     * The result is determined by bot settings and is not influences by any guild specific settings.
     * See {@link #canUse(String, Member)} and {@link #canUseInChannel(String, Member, Guild, MessageChannel)}
     * for guild specific checks.
     *
     * @param command command to check for
     * @param member  member to check
     * @return true if a user can access a command on his guild.
     */
    public boolean canAccess(Command command, Member member) {
        return canAccess(command, member.getUser(), member.getGuild());
    }
    /**
     * Checks if a user can access a command.
     * The result is determined by bot settings and is not influences by any guild specific settings.
     * See {@link #canUse(String, Member)} and {@link #canUseInChannel(String, Member, Guild, MessageChannel)}
     * for guild specific checks.
     *
     * @param command command to check for
     * @param user  user to check
     * @return true if a user can access a command on his guild.
     */
    public boolean canAccess(Command command, User user) {
        return canAccess(command, user, null);
    }

    /**
     * Checks if a user or guild can access a command.
     * The result is determined by bot settings and is not influences by any guild specific settings.
     * See {@link #canUse(String, Member)} and {@link #canUseInChannel(String, Member, Guild, MessageChannel)}
     * for guild specific checks.
     *
     * @param command command to check for
     * @param user  user to check
     * @return true if a user can access a command on his guild.
     */
    private boolean canAccess(Command command, User user, Guild guild) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.can_access(?,?,?)")) {
            statement.setString(1, command.getCommandIdentifier());
            statement.setLong(2, guild == null ? 0L : guild.getIdLong());
            statement.setLong(3, user.getIdLong());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(command + " has no complete setup");
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
    public boolean canUseInChannel(String command, Member member, Guild guild, MessageChannel channel) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.can_use_in_channel(?,?,?,?,?)")) {
            statement.setString(1, command);
            statement.setLong(2, guild.getIdLong());
            statement.setLong(3, member.getIdLong());
            statement.setLong(4, channel.getIdLong());
            Long[] roles = new Long[member.getRoles().size()];
            Array roleIds = conn.createArrayOf("bigint", member.getRoles()
                    .stream().map(ISnowflake::getIdLong).collect(Collectors.toList()).toArray(roles));
            statement.setArray(5, roleIds);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(command + " has no complete setup");
    }

    /**
     * Checks if a command should be displayed in help.
     * A command is displayed if the {@link #canUseInChannel(String, Member, Guild, MessageChannel)} and
     * {@link #getState(Command, Guild)} return true.
     *
     * @param command command identifier. A command identifier can be the root
     *                command {@link Command#getCommandIdentifier()} or a string containing the command
     *                and the {@link SubCommand#getSubCommandIdentifier()} separated by a ".".
     * @param member  member to check for
     * @param guild   guild to check for
     * @param channel channel to check for
     * @return true if a command should be displayed.
     */
    public boolean isDisplayedInHelp(String command, Member member, Guild guild, MessageChannel channel) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.help_display_check(?,?,?,?,?,?)")) {
            statement.setString(1, command);
            statement.setLong(2, guild.getIdLong());
            statement.setLong(3, member.getIdLong());
            statement.setLong(4, channel.getIdLong());
            Array roleIds = getSnowflakeArray(member.getRoles(), conn);
            statement.setArray(5, roleIds);
            statement.setBoolean(6, member.hasPermission(Permission.ADMINISTRATOR));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(command + " has no complete setup");
    }

    /**
     * Add a channel to channel list for channel specific command restriction.
     *
     * @param command command to set
     * @param guild   guild to set
     * @param channel channel to add
     * @return true if the database access was successful
     */
    public boolean addChannel(Command command, Guild guild, TextChannel channel) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.add_command_channel(?,?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setLong(2, guild.getIdLong());
            statement.setLong(3, channel.getIdLong());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, null);
            return false;
        }
        return true;
    }

    /**
     * Removes a channel from channel list for channel specific command restriction.
     *
     * @param command command for lookup
     * @param guild   guild for lookup
     * @param channel channel to remove
     * @return true if the database access was successful
     */
    public boolean removeChannel(Command command, Guild guild, TextChannel channel) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.remove_command_channel(?,?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setLong(2, guild.getIdLong());
            statement.setLong(3, channel.getIdLong());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, null);
            return false;
        }
        return true;
    }

    /**
     * Get the channel list type.
     *
     * @param command command to check for
     * @param guild   guild to check for
     * @return list type of the channel list for this command
     */
    public ListType getChannelListType(Command command, Guild guild) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.get_channel_list_type(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setLong(2, guild.getIdLong());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return ListType.getType(resultSet.getString(1));
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    /**
     * Check if the channel check is active for this command.
     *
     * @param command command to check for
     * @param guild   guild to check for
     * @return true if the channel check is active
     */
    public boolean isChannelCheckActive(Command command, Guild guild) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.is_channel_check_active(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setLong(2, guild.getIdLong());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    /**
     * Set the state of a command on a guild.
     * The state defines if a command can be used on this guild or not.
     *
     * @param command command to set the state for
     * @param guild   guild where the state should be set
     * @param state   state of the command
     * @return true if the query execution was successful.
     */
    public boolean setState(Command command, Guild guild, boolean state) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.set_command_state(?,?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setLong(2, guild.getIdLong());
            statement.setBoolean(3, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, null);
            return false;
        }
        return true;
    }

    /**
     * Activates or deactivates the channel check for this command.
     *
     * @param command command to change
     * @param guild   guild to activate the check
     * @param state   true when guild should be checked
     * @return true if the query execution was successful
     */
    public boolean setChannelCheckActive(Command command, Guild guild, boolean state) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.set_command_channel_check_active(?,?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setLong(2, guild.getIdLong());
            statement.setBoolean(3, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, null);
            return false;
        }
        return true;
    }

    /**
     * Changes the channel list type.
     *
     * @param context  context to change
     * @param guild    guild to set the list type
     * @param listType ListType enum.
     * @return true if the query execution was successful
     */
    public boolean setChannelListType(Command context, Guild guild, ListType listType) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.set_command_channel_list_type(?,?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setLong(2, guild.getIdLong());
            statement.setString(3, listType.toString());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, null);
            return false;
        }
        return true;
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
    public boolean getState(Command command, Guild guild) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.get_command_state(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setLong(2, guild.getIdLong());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        return false;
    }

    /**
     * Get a list of all channels where the command is enabled or disabled.
     * Wether a command is enabled or disabled is defined by the {@link ListType} retrived
     * by {@link #getChannelListType(Command, Guild)}.
     *
     * @param command command to get the list for
     * @param guild   guild to get the list for
     * @return list of channel ids. can be empty
     */
    public List<String> getChannelList(Command command, Guild guild) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.get_command_channel_list(?,?)")) {
            statement.setString(1, command.getCommandIdentifier());
            statement.setLong(2, guild.getIdLong());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Array array = resultSet.getArray(1);
                if (array == null) {
                    return Collections.emptyList();
                }
                Long[] idArray = (Long[]) array.getArray();
                return Arrays.stream(idArray).map(String::valueOf).collect(Collectors.toList());
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(command + " has no complete setup");
    }

    /**
     * Definies if a command or a subcommand requires a permission.
     * If the command requires a permission all subcommand require a permission too.
     * If a command does not require a permission, a subcommand can require a permission.
     * Wether a command or subcommand require a permission is changed by
     * the {@link #setPermissionOverride(String, boolean, Guild, EventWrapper)} Method.
     *
     * @param command command identifier. A command identifier can be the root
     *                command {@link Command#getCommandIdentifier()} or a string containing the command
     *                and the {@link SubCommand#getSubCommandIdentifier()} separated by a ".".
     * @param guild   the guild to check.
     * @return true if a command requires a permission.
     */
    public boolean requiresPermission(String command, Guild guild) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("select shepard_settings.requires_permission(?,?)")) {
            statement.setString(1, command);
            statement.setLong(2, guild.getIdLong());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        return false;
    }
}
