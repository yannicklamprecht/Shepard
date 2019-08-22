package de.chojo.shepard.database.queries;

import de.chojo.shepard.database.DatabaseConnector;
import de.chojo.shepard.database.ListType;
import de.chojo.shepard.database.types.ContextData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.chojo.shepard.database.DbUtil.getIdRaw;
import static de.chojo.shepard.database.DbUtil.handleException;

@SuppressWarnings("SqlDialectInspection")
public final class Context {
    //Map with context
    private static Map<String, Map<String, List<String>>> userPermissions = new HashMap<>();
    private static Map<String, Map<String, List<String>>> rolePermissions = new HashMap<>();
    private static Map<String, ContextData> contextData = new HashMap<>();

    private static Map<String, Boolean> contextDataDirty = new HashMap<>();
    private static Map<String, Boolean> userPermissionDirty = new HashMap<>();
    private static Map<String, Boolean> rolePermissionDirty = new HashMap<>();

    public static void addContextUser(String contextName, String userId) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.add_context_user(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, getIdRaw(userId));
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public static void removeContextUser(String contextName, String userId) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_context_user(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, getIdRaw(userId));
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public static void addContextGuild(String contextName, String guildId) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.add_context_guild(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, getIdRaw(guildId));
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public static void removeContextGuild(String contextName, String guildId) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_context_guild(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, getIdRaw(guildId));
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public static void addContextUserPermission(String contextName, String guildId, String userId) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.add_context_user_permission(?,?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, getIdRaw(guildId));
            statement.setString(3, getIdRaw(userId));
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public static void removeContextUserPermission(String contextName, String guildId, String userId) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_context_user_permission(?,?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, getIdRaw(guildId));
            statement.setString(3, getIdRaw(userId));
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public static void addContextRolePermission(String contextName, String guildId, String userId) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.add_context_role_permission(?,?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, getIdRaw(guildId));
            statement.setString(3, getIdRaw(userId));
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public static void removeContextRolePermission(String contextName, String guildId, String userId) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_context_role_permission(?,?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, getIdRaw(guildId));
            statement.setString(3, getIdRaw(userId));
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }


    public static void setContextAdmin(String contextName, boolean state) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_context_admin(?,?)")) {
            statement.setString(1, contextName);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public static void setContextNsfw(String contextName, boolean state) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_context_nsfw(?,?)")) {
            statement.setString(1, contextName);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public static void setContextCharacterCheckActive(String contextName, boolean state) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_context_character_check_active(?,?)")) {
            statement.setString(1, contextName);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public static void setContextGuildCheckActive(String contextName, boolean state) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_context_guild_check_active(?,?)")) {
            statement.setString(1, contextName);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public static void setContextCharacterListType(String contextName, ListType listType) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_context_character_list_type(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, listType.toString());
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public static void setContextGuildListType(String contextName, ListType listType) {
        contextDataDirty.put(contextName, true);

        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_context_guild_list_type(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, listType.toString());
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public static ContextData getContextData(String contextName) {
        if (contextDataDirty.containsKey(contextName)) {
            if (!contextDataDirty.get(contextName)) {
                return contextData.get(contextName);
            }
        }

        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT * from shepard_func.get_context_data(?)")) {
            statement.setString(1, contextName);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                ContextData data = new ContextData();

                data.setAdmin_only(result.getBoolean("admin_only"));
                data.setNsfw(result.getBoolean("nsfw"));
                data.setUserCheckActive(result.getBoolean("user_check_active"));
                data.setUserListType(ListType.getType(result.getString("user_list_type")));
                if (result.getArray("user_list") == null) {
                    data.setUserList(new String[0]);
                } else {
                    data.setUserList((String[]) result.getArray("user_list").getArray());
                }
                data.setGuildCheckActive(result.getBoolean("guild_check_active"));
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
            handleException(e);
        }

        return contextData.getOrDefault(contextName, null);
    }

    public static Map<String, List<String>> getContextUserPermissions(String contextName) {
        if (userPermissions.containsKey(contextName)) {
            if (!userPermissionDirty.get(contextName)) {
                return userPermissions.get(contextName);
            }
        }

        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT * from shepard_func.get_context_user_permissions(?)")) {
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
            handleException(e);
        }
        return userPermissions.getOrDefault(contextName, Collections.emptyMap());
    }

    public static Map<String, List<String>> getContextRolePermissions(String contextName) {
        if (rolePermissions.containsKey(contextName)) {
            if (!rolePermissionDirty.get(contextName)) {
                return rolePermissions.get(contextName);
            }
        }


        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT * from shepard_func.get_context_role_permissions(?)")) {
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
            handleException(e);
        }
        return rolePermissions.getOrDefault(contextName, Collections.emptyMap());
    }
}
