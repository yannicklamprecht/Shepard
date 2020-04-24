package de.eldoria.shepard.commandmodules.commandsettings.data;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.commandsettings.types.CommandSettings;
import de.eldoria.shepard.commandmodules.commandsettings.types.ListType;
import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
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
import java.util.Optional;
import java.util.stream.Collectors;

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
    public boolean addCommandUser(Command context, User user, MessageEventDataWrapper messageContext) {
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
    public boolean removeContextUser(Command context, User user,
                                     MessageEventDataWrapper messageContext) {
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
    public boolean addContextGuild(Command context, Guild guild,
                                   MessageEventDataWrapper messageContext) {
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
     * Removes a guild from the context list.
     *
     * @param context        context to change
     * @param guild          guild id to remove
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean removeContextGuild(Command context, Guild guild,
                                      MessageEventDataWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

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
     * Adds a context user permission on a guild.
     *
     * @param context        context to change
     * @param guild          guild id where the permission should be added
     * @param user           user which should be added
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean addContextUserPermission(Command context, Guild guild,
                                            User user, MessageEventDataWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.add_command_user_permission(?,?,?)")) {
            statement.setString(1, commandIdentifier);
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
     * Removes a context user permission on a guild.
     *
     * @param context        context to change
     * @param guild          guild id where the permission should be removed
     * @param user           user which should be removed
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean removeContextUserPermission(Command context, Guild guild,
                                               User user, MessageEventDataWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.remove_command_user_permission(?,?,?)")) {
            statement.setString(1, commandIdentifier);
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
     * Adds a context role permission on a guild.
     *
     * @param context        context to change
     * @param guild          guild id where the permission should be added
     * @param role           role which should be added
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean addContextRolePermission(Command context, Guild guild,
                                            Role role, MessageEventDataWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.add_command_role_permission(?,?,?)")) {
            statement.setString(1, commandIdentifier);
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
     * Removes a context role permission on a guild.
     *
     * @param context        context to change
     * @param guild          guild id where the permission should be removed
     * @param role           role which should be removed
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean removeContextRolePermission(Command context, Guild guild,
                                               Role role, MessageEventDataWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.remove_command_role_permission(?,?,?)")) {
            statement.setString(1, commandIdentifier);
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
    public boolean setContextAdmin(Command context, boolean state,
                                   MessageEventDataWrapper messageContext) {
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
    public boolean setContextNsfw(Command context, boolean state,
                                  MessageEventDataWrapper messageContext) {
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
    public boolean setContextUserCheckActive(Command context, boolean state,
                                             MessageEventDataWrapper messageContext) {
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
    public boolean setContextGuildCheckActive(Command context, boolean state,
                                              MessageEventDataWrapper messageContext) {
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
    public boolean setContextUserListType(Command context, ListType listType,
                                          MessageEventDataWrapper messageContext) {
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
    public boolean setContextGuildListType(Command context, ListType listType,
                                           MessageEventDataWrapper messageContext) {
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
    public boolean setContextGuildCooldown(Command context, int seconds,
                                           MessageEventDataWrapper messageContext) {
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
    public boolean setContextUserCooldown(Command context, int seconds,
                                          MessageEventDataWrapper messageContext) {
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
     * specific {@link Command} Context name.
     *
     * @param context        context to change.
     * @param state          state
     * @param guild          guild to add
     * @param messageContext message context for error handling
     * @return true if the database access was successful
     */
    public boolean setPermissionOverride(Command context, boolean state,
                                         Guild guild, MessageEventDataWrapper messageContext) {
        String commandIdentifier = context.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.set_permission_override(?,?,?)")) {
            statement.setString(1, commandIdentifier);
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
    public CommandSettings getCommandData(Command context, MessageEventDataWrapper messageContext) {
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
    public boolean isNsfw(Command command) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.is_nsfw(?)")) {
            statement.setString(1, commandIdentifier);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    public boolean isAdminCommand(Command command) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.is_admin_command(?)")) {
            statement.setString(1, commandIdentifier);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    public boolean isUserCheckActive(Command command) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.is_user_check_active(?)")) {
            statement.setString(1, commandIdentifier);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    public boolean isUserOnList(Command command, User user) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.is_user_on_list(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setLong(2, user.getIdLong());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    public ListType getUserListType(Command command) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.get_user_list_type(?)")) {
            statement.setString(1, commandIdentifier);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return ListType.getType(resultSet.getString(1));
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    public boolean isGuildCheckActive(Command command) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.is_guild_check_active(?)")) {
            statement.setString(1, commandIdentifier);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    public boolean isGuildOnList(Command command, Guild guild) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.is_guild_on_list(?,?)")) {
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

    public ListType getGuildListType(Command command) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.get_guild_list_type(?)")) {
            statement.setString(1, commandIdentifier);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return ListType.getType(resultSet.getString(1));
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    public boolean hasCooldown(Command command) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.has_cooldown(?)")) {
            statement.setString(1, commandIdentifier);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    public boolean hasGuildCooldown(Command command) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.has_guild_cooldown(?)")) {
            statement.setString(1, commandIdentifier);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    public boolean hasUserCooldown(Command command) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.has_user_cooldown(?)")) {
            statement.setString(1, commandIdentifier);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    public int getUserCooldown(Command command) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.get_user_cooldown(?)")) {
            statement.setString(1, commandIdentifier);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    public int getGuildCooldown(Command command) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.get_guild_cooldown(?)")) {
            statement.setString(1, commandIdentifier);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    // Server settings
    public Optional<Boolean> getPermissionOverride(Command command, Guild guild) {
        if (!hasPermissionOverride(command, guild)) {
            return Optional.empty();
        }

        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.get_permission_override(?)")) {
            statement.setString(1, commandIdentifier);
            statement.setLong(2, guild.getIdLong());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(resultSet.getBoolean(1));
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        return Optional.of(true);
    }

    public boolean hasPermissionOverride(Command command, Guild guild) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.has_permission_override(?,?)")) {
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

    public boolean hasUserPermission(Command command, Guild guild, User user) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.has_user_permission(?,?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setLong(2, guild.getIdLong());
            statement.setLong(3, user.getIdLong());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    public boolean hasUserPermissionRole(Command command, Member member) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.has_user_permission_role(?,?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setLong(2, member.getGuild().getIdLong());
            Long[] roles = new Long[member.getRoles().size()];
            Array roleIds = conn.createArrayOf("bigint", member.getRoles()
                    .stream().map(ISnowflake::getIdLong).collect(Collectors.toList()).toArray(roles));
            statement.setArray(3, roleIds);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    public boolean requirePermission(Command command, Guild guild) {
        Optional<Boolean> permissionOverride = getPermissionOverride(command, guild);
        return permissionOverride.orElseGet(() -> isAdminCommand(command));
    }

    public List<String> getUserPermissionList(Command command, Guild guild) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.get_user_permission_list(?,?)")) {
            statement.setString(1, commandIdentifier);
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
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    public List<String> getRolePermissionList(Command command, Guild guild) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.get_role_permission_list(?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setLong(2, guild.getIdLong());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Arrays.stream((Long[]) resultSet.getArray(1).getArray()).map(String::valueOf).collect(Collectors.toList());
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    public boolean canUse(Command command, Member member) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.can_use(?,?,?,?)")) {
            statement.setString(1, commandIdentifier);
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
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

    public boolean canAccess(Command command, Member member) {
        String commandIdentifier = command.getCommandIdentifier();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_settings.can_access(?,?,?)")) {
            statement.setString(1, commandIdentifier);
            statement.setLong(2, member.getGuild().getIdLong());
            statement.setLong(3, member.getIdLong());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        throw new RuntimeException(commandIdentifier + " has no complete setup");
    }

}
