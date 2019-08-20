package de.chojo.shepard.database.queries;

import de.chojo.shepard.database.DatabaseConnector;
import de.chojo.shepard.database.ListType;
import de.chojo.shepard.database.types.ContextData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.chojo.shepard.database.DbUtil.getIdRaw;
import static de.chojo.shepard.database.DbUtil.handleException;

public class Context {
    public void addContextCharacter(String contextName, String userId) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.add_context_character(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, getIdRaw(userId));
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public void removeContextCharacter(String contextName, String userId) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_context_character(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, getIdRaw(userId));
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public void addContextGuild(String contextName, String guildId) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.add_context_guild(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, getIdRaw(guildId));
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public void removeContextGuild(String contextName, String guildId) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_context_guild(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, getIdRaw(guildId));
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public void addContextPermission(String contextName, String guildId, String userId) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.add_context_permission(?,?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, getIdRaw(guildId));
            statement.setString(3, getIdRaw(userId));
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public void removeContextPermission(String contextName, String guildId, String userId) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_context_permission(?,?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, getIdRaw(guildId));
            statement.setString(3, getIdRaw(userId));
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public void setContextAdmin(String contextName, boolean state) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_context_admin(?,?)")) {
            statement.setString(1, contextName);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public void setContextNsfw(String contextName, boolean state) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_context_nsfw(?,?)")) {
            statement.setString(1, contextName);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public void setContextCharacterCheckActive(String contextName, boolean state) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_context_character_check_active(?,?)")) {
            statement.setString(1, contextName);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public void setContextGuildCheckActive(String contextName, boolean state) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_context_guild_check_active(?,?)")) {
            statement.setString(1, contextName);
            statement.setBoolean(2, state);
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public void setContextCharacterListType(String contextName, ListType listType) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_context_character_list_type(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, listType.toString());
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public void setContextGuildListType(String contextName, ListType listType) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_context_guild_list_type(?,?)")) {
            statement.setString(1, contextName);
            statement.setString(2, listType.toString());
            statement.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public ContextData getContextData(String contextName) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT * from shepard_func.get_context_data(?)")) {
            statement.setString(1, contextName);
            ResultSet result = statement.executeQuery();

            ContextData data = new ContextData();
            data.setAdmin_only(result.getBoolean("admin_only"));
            data.setNsfw(result.getBoolean("nsfw"));
            data.setCharacterCheckActive(result.getBoolean("character_check_active"));
            data.setCharacterListType(ListType.getType(result.getString("character_list_type")));
            data.setCharacterList((String[]) result.getArray("characters_list").getArray());
            data.setGuildCheckActive(result.getBoolean("guild_check_active"));
            data.setGuildListType(ListType.getType(result.getString("guild_list_type")));
            data.setGuildList((String[]) result.getArray("guild_list").getArray());

            return data;

        } catch (SQLException e) {
            handleException(e);
        }

        return null;
    }

    public Map<String, List<String>> getContextPermissions(String contextName) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT * from shepard_func.get_context_permissions(?)")) {
            statement.setString(1, contextName);
            ResultSet result = statement.executeQuery();

            HashMap<String, List<String>> data = new HashMap<>();

            while (result.next()){
                String guild = result.getString("guild_id");
                String user = result.getString("user_id");

                if(data.containsKey(guild)){
                    data.get(guild).add(user);
                }else{
                    data.put(guild, new ArrayList<>(List.of(user)));
                }
            }

            return data;

        } catch (SQLException e) {
            handleException(e);
        }
        return null;
    }

}
