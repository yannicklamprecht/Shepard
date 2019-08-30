package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.database.ListType;
import de.eldoria.shepard.database.types.ContextSettings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.eldoria.shepard.database.DbUtil.handleException;

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
     * @param contextName context zo change
     * @param user      user to add
     * @param event       event from command sending for error handling. Can be null.
     */
    public static void addContextUser(String contextName, User user, MessageReceivedEvent event) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_context_user(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, user.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Removes a user from the context list.
     *
     * @param contextName context name to change
     * @param user      user to remove
     * @param event       event from command sending for error handling. Can be null.
     */
    public static void removeContextUser(String contextName, User user, MessageReceivedEvent event) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_context_user(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, user.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Adds a guild to the context list.
     *
     * @param contextName context name to change
     * @param guild     guild id to add
     * @param event       event from command sending for error handling. Can be null.
     */
    public static void addContextGuild(String contextName, Guild guild, MessageReceivedEvent event) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_context_guild(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, guild.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Removes a guild from the context list.
     *
     * @param contextName context name to change
     * @param guild     guild id to remove
     * @param event       event from command sending for error handling. Can be null.
     */
    public static void removeContextGuild(String contextName, Guild guild, MessageReceivedEvent event) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_context_guild(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, guild.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Adds a context user permission on a guild.
     *
     * @param contextName context name to change
     * @param guild       guild id where the permission should be added
     * @param user      user which should be added
     * @param event       event from command sending for error handling. Can be null.
     */
    public static void addContextUserPermission(String contextName, Guild guild,
                                                User user, MessageReceivedEvent event) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_context_user_permission(?,?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, guild.getId());
            statement.setString(3, user.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Removes a context user permission on a guild.
     *
     * @param contextName context name to change
     * @param guild       guild id where the permission should be removed
     * @param user      user which should be removed
     * @param event       event from command sending for error handling. Can be null.
     */
    public static void removeContextUserPermission(String contextName, Guild guild,
                                                   User user, MessageReceivedEvent event) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_context_user_permission(?,?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, guild.getId());
            statement.setString(3, user.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Adds a context role permission on a guild.
     *
     * @param contextName context name to change
     * @param guild       guild id where the permission should be added
     * @param role      role which should be added
     * @param event       event from command sending for error handling. Can be null.
     */
    public static void addContextRolePermission(String contextName, Guild guild,
                                                Role role, MessageReceivedEvent event) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_context_role_permission(?,?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, guild.getId());
            statement.setString(3, role.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Removes a context role permission on a guild.
     *
     * @param contextName context name to change
     * @param guild       guild id where the permission should be removed
     * @param role      role which should be removed
     * @param event       event from command sending for error handling. Can be null.
     */
    public static void removeContextRolePermission(String contextName, Guild guild,
                                                    Role role, MessageReceivedEvent event) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_context_role_permission(?,?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, guild.getId());
            statement.setString(3, role.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Changes the context admin only state.
     *
     * @param contextName Name of the context to change
     * @param state       True if it is a admin only command.
     * @param event       event from command sending for error handling. Can be null.
     */
    public static void setContextAdmin(String contextName, boolean state, MessageReceivedEvent event) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_context_admin(?,?)")) {
            statement.setString(1, contextName);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Changes the context nsfw state.
     *
     * @param contextName Name of the context to change
     * @param state       True if it is a nsfw command.
     * @param event       event from command sending for error handling. Can be null.
     */
    public static void setContextNsfw(String contextName, boolean state, MessageReceivedEvent event) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_context_nsfw(?,?)")) {
            statement.setString(1, contextName);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Activates or deactivates the user check for this context.
     *
     * @param contextName Name of the context to change
     * @param state       true when user should be checked
     * @param event       event from command sending for error handling. Can be null.
     */
    public static void setContextUserCheckActive(String contextName, boolean state, MessageReceivedEvent event) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_context_user_check_active(?,?)")) {
            statement.setString(1, contextName);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Activates or deactivates the guild check for this context.
     *
     * @param contextName Name of the context to change
     * @param state       true when guild should be checked
     * @param event       event from command sending for error handling. Can be null.
     */
    public static void setContextGuildCheckActive(String contextName, boolean state, MessageReceivedEvent event) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_context_guild_check_active(?,?)")) {
            statement.setString(1, contextName);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Changes the context user list type.
     *
     * @param contextName Name of the context to change
     * @param listType    ListType enum.
     * @param event       event from command sending for error handling. Can be null.
     */
    public static void setContextUserListType(String contextName, ListType listType, MessageReceivedEvent event) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_context_user_list_type(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, listType.toString());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Changes the context guild list type.
     *
     * @param contextName Name of the context to change
     * @param listType    ListType enum.
     * @param event       event from command sending for error handling. Can be null.
     */
    public static void setContextGuildListType(String contextName, ListType listType, MessageReceivedEvent event) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_context_guild_list_type(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, listType.toString());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Returns the context data of the needed context.
     *
     * @param contextName Name of the context for lookup
     * @param event       event from command sending for error handling. Can be null.
     * @return Context data object.
     */
    public static ContextSettings getContextData(String contextName, MessageReceivedEvent event) {
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

                contextData.put(contextName, data);
                contextDataDirty.put(contextName, false);
            }

        } catch (SQLException e) {
            handleException(e, event);
        }

        return contextData.getOrDefault(contextName, null);
    }

    /**
     * Returns a map which contains a list of all users per guild, which are allowed to use this context.
     *
     * @param contextName Name of the context for permission lookup.
     * @param event       event from command sending for error handling. Can be null.
     * @return Map [guild_id, List(role_ids)] Map which contains the user as list for each guild
     */
    public static Map<String, List<String>> getContextUserPermissions(String contextName, MessageReceivedEvent event) {
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
            handleException(e, event);
        }
        return userPermissions.getOrDefault(contextName, Collections.emptyMap());
    }

    /**
     * Returns a map which contains a list of all roles per guild, which are allowed to use this context.
     *
     * @param contextName Name of the context for permission lookup.
     * @param event       event from command sending for error handling. Can be null.
     * @return Map [guild_id, List(role_ids)] Map which contains the user as list for each guild
     */
    public static Map<String, List<String>> getContextRolePermissions(String contextName, MessageReceivedEvent event) {
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
            handleException(e, event);
        }
        return rolePermissions.getOrDefault(contextName, Collections.emptyMap());
    }
}
