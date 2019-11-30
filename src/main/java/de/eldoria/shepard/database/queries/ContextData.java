package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.database.ListType;
import de.eldoria.shepard.database.types.ContextSettings;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.eldoria.shepard.database.DbUtil.handleExceptionAndIgnore;

public final class ContextData {
    //Map with context
    private static final Map<String, Map<String, List<String>>> userPermissions = new HashMap<>();
    private static final Map<String, Map<String, List<String>>> rolePermissions = new HashMap<>();
    private static final Map<String, ContextSettings> contextData = new HashMap<>();

    private static final Map<String, Boolean> contextDataDirty = new HashMap<>();
    private static final Map<String, Boolean> userPermissionDirty = new HashMap<>();
    private static final Map<String, Boolean> rolePermissionDirty = new HashMap<>();

    private ContextData() {
    }

    /**
     * Adds a user to a context list.
     *
     * @param contextName    context zo change
     * @param user           user to add
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean addContextUser(String contextName, User user, MessageEventDataWrapper messageContext) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_context_user(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, user.getId());
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Removes a user from the context list.
     *
     * @param contextName    context name to change
     * @param user           user to remove
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean removeContextUser(String contextName, User user, MessageEventDataWrapper messageContext) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_context_user(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, user.getId());
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Adds a guild to the context list.
     *
     * @param contextName    context name to change
     * @param guild          guild id to add
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean addContextGuild(String contextName, Guild guild, MessageEventDataWrapper messageContext) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_context_guild(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, guild.getId());
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Removes a guild from the context list.
     *
     * @param contextName    context name to change
     * @param guild          guild id to remove
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean removeContextGuild(String contextName, Guild guild, MessageEventDataWrapper messageContext) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_context_guild(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, guild.getId());
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Adds a context user permission on a guild.
     *
     * @param contextName    context name to change
     * @param guild          guild id where the permission should be added
     * @param user           user which should be added
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean addContextUserPermission(String contextName, Guild guild,
                                                   User user, MessageEventDataWrapper messageContext) {
        userPermissionDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_context_user_permission(?,?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, guild.getId());
            statement.setString(3, user.getId());
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Removes a context user permission on a guild.
     *
     * @param contextName    context name to change
     * @param guild          guild id where the permission should be removed
     * @param user           user which should be removed
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean removeContextUserPermission(String contextName, Guild guild,
                                                      User user, MessageEventDataWrapper messageContext) {
        userPermissionDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_context_user_permission(?,?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, guild.getId());
            statement.setString(3, user.getId());
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Adds a context role permission on a guild.
     *
     * @param contextName    context name to change
     * @param guild          guild id where the permission should be added
     * @param role           role which should be added
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean addContextRolePermission(String contextName, Guild guild,
                                                   Role role, MessageEventDataWrapper messageContext) {
        rolePermissionDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_context_role_permission(?,?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, guild.getId());
            statement.setString(3, role.getId());
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Removes a context role permission on a guild.
     *
     * @param contextName    context name to change
     * @param guild          guild id where the permission should be removed
     * @param role           role which should be removed
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean removeContextRolePermission(String contextName, Guild guild,
                                                      Role role, MessageEventDataWrapper messageContext) {
        rolePermissionDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_context_role_permission(?,?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, guild.getId());
            statement.setString(3, role.getId());
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Changes the context admin only state.
     *
     * @param contextName    Name of the context to change
     * @param state          True if it is a admin only command.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean setContextAdmin(String contextName, boolean state, MessageEventDataWrapper messageContext) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_context_admin(?,?)")) {
            statement.setString(1, contextName);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Changes the context nsfw state.
     *
     * @param contextName    Name of the context to change
     * @param state          True if it is a nsfw command.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean setContextNsfw(String contextName, boolean state, MessageEventDataWrapper messageContext) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_context_nsfw(?,?)")) {
            statement.setString(1, contextName);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Activates or deactivates the user check for this context.
     *
     * @param contextName    Name of the context to change
     * @param state          true when user should be checked
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean setContextUserCheckActive(String contextName, boolean state,
                                                    MessageEventDataWrapper messageContext) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_context_user_check_active(?,?)")) {
            statement.setString(1, contextName);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Activates or deactivates the guild check for this context.
     *
     * @param contextName    Name of the context to change
     * @param state          true when guild should be checked
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean setContextGuildCheckActive(String contextName, boolean state,
                                                     MessageEventDataWrapper messageContext) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_context_guild_check_active(?,?)")) {
            statement.setString(1, contextName);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Changes the context user list type.
     *
     * @param contextName    Name of the context to change
     * @param listType       ListType enum.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean setContextUserListType(String contextName, ListType listType,
                                                 MessageEventDataWrapper messageContext) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_context_user_list_type(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, listType.toString());
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Changes the context guild list type.
     *
     * @param contextName    Name of the context to change
     * @param listType       ListType enum.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean setContextGuildListType(String contextName, ListType listType,
                                                  MessageEventDataWrapper messageContext) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_context_guild_list_type(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, listType.toString());
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Set the global guild cooldown to a context.
     *
     * @param contextName    name of the context
     * @param seconds        cooldown in seconds
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean setContextGuildCooldown(String contextName, int seconds,
                                                  MessageEventDataWrapper messageContext) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_guild_cooldown(?,?)")) {
            statement.setString(1, contextName);
            statement.setInt(2, seconds);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Set the global user cooldown to a context.
     *
     * @param contextName    name of the context
     * @param seconds        cooldown in seconds
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean setContextUserCooldown(String contextName, int seconds,
                                                 MessageEventDataWrapper messageContext) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_user_cooldown(?,?)")) {
            statement.setString(1, contextName);
            statement.setInt(2, seconds);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Returns the context data of the needed context.
     *
     * @param contextName    Name of the context for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return Context data object.
     */
    public static ContextSettings getContextData(String contextName, MessageEventDataWrapper messageContext) {
        if (contextDataDirty.containsKey(contextName)) {
            if (!contextDataDirty.get(contextName)) {
                return contextData.get(contextName);
            }
        }

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_context_data(?)")) {
            statement.setString(1, contextName);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                ContextSettings data = new ContextSettings();

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

                contextData.put(contextName, data);
                contextDataDirty.put(contextName, false);
            }

        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
        }

        return contextData.getOrDefault(contextName, null);
    }

    /**
     * Returns a list of all user ids, which have access to the context on a specific guild.
     *
     * @param guild          Guild for lookup.
     * @param contextName    Name of the context for permission lookup.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return List of user ids
     */
    public static List<String> getContextUserPermission(Guild guild, String contextName,
                                                        MessageEventDataWrapper messageContext) {
        return getContextUserPermissions(contextName, messageContext).getOrDefault(guild.getId(),
                Collections.emptyList());
    }

    /**
     * Returns a map which contains a list of all users per guild, which are allowed to use this context.
     *
     * @param contextName    Name of the context for permission lookup.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return Map [guild_id, List(role_ids)] Map which contains the user as list for each guild
     */
    public static Map<String, List<String>> getContextUserPermissions(String contextName,
                                                                      MessageEventDataWrapper messageContext) {
        if (userPermissions.containsKey(contextName)) {
            if (!userPermissionDirty.get(contextName)) {
                return userPermissions.get(contextName);
            }
        }

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_context_user_permissions(?)")) {
            statement.setString(1, contextName);
            ResultSet result = statement.executeQuery();

            HashMap<String, List<String>> data = new HashMap<>();

            while (result.next()) {
                String guild = result.getString("guild_id");
                String user = result.getString("user_id");

                if (data.containsKey(guild)) {
                    data.get(guild).add(user);
                } else {
                    data.put(guild, new ArrayList<>(List.of(user)));
                }
            }

            userPermissions.put(contextName, data);
            userPermissionDirty.put(contextName, false);
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
        }
        if (userPermissions.containsKey(contextName)) {
            if (messageContext != null
                    && !userPermissions.get(contextName).containsKey(messageContext.getGuild().getId())) {
                userPermissions.get(contextName).put(messageContext.getGuild().getId(), Collections.emptyList());
            }
        }

        return userPermissions.get(contextName);
    }

    /**
     * Returns a list of all role ids, which have access to the context on a specific guild.
     *
     * @param guild          Guild for lookup.
     * @param contextName    Name of the context for permission lookup.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return List of user ids
     */
    public static List<String> getContextRolePermission(Guild guild, String contextName,
                                                        MessageEventDataWrapper messageContext) {
        return getContextRolePermissions(contextName, messageContext)
                .getOrDefault(guild.getId(), Collections.emptyList());
    }


    /**
     * Returns a map which contains a list of all roles per guild, which are allowed to use this context.
     *
     * @param contextName    Name of the context for permission lookup.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return Map [guild_id, List(role_ids)] Map which contains the user as list for each guild
     */
    public static Map<String, List<String>> getContextRolePermissions(String contextName,
                                                                      MessageEventDataWrapper messageContext) {
        if (rolePermissions.containsKey(contextName)) {
            if (!rolePermissionDirty.get(contextName)) {
                return rolePermissions.get(contextName);
            }
        }


        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_context_role_permissions(?)")) {
            statement.setString(1, contextName);
            ResultSet result = statement.executeQuery();

            HashMap<String, List<String>> data = new HashMap<>();

            while (result.next()) {
                String guild = result.getString("guild_id");
                String role = result.getString("role_id");

                if (data.containsKey(guild)) {
                    data.get(guild).add(role);
                } else {
                    data.put(guild, new ArrayList<>(List.of(role)));
                }
            }

            rolePermissions.put(contextName, data);
            rolePermissionDirty.put(contextName, false);
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
        }

        if (rolePermissions.containsKey(contextName)) {
            if (messageContext != null
                    && !rolePermissions.get(contextName).containsKey(messageContext.getGuild().getId())) {
                rolePermissions.get(contextName).put(messageContext.getGuild().getId(), Collections.emptyList());
            }
        }

        return rolePermissions.getOrDefault(contextName, Collections.emptyMap());
    }
}
