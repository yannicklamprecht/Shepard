package de.chojo.shepard.database.queries;

import de.chojo.shepard.database.DatabaseConnector;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.chojo.shepard.database.DbUtil.handleException;

public class Mutes {
    private static Map<String, List<String>> mutedUsers = new HashMap<>();
    private static Map<String, Boolean> mutedUsersDirty = new HashMap<>();
    private static LocalDateTime lastRefresh;

    public static void setMuted(String guildId, String userId, String duration, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_muted(?,?,?)")) {
            statement.setString(1, guildId);
            statement.setString(2, userId);
            statement.setString(3, duration);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }

        mutedUsersDirty.put(guildId, true);
    }

    private static List<String> refreshGuildData(String guildId, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.get_muted_users(?)")) {
            statement.setString(1, guildId);
            ResultSet result = statement.executeQuery();
            if (result.getArray(1) != null) {

                mutedUsers.put(guildId, Arrays.asList((String[]) result.getArray(1).getArray()));
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return Collections.emptyList();
    }

    public static void removeMute(String guildId, String userId, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_mute(?,?)")) {
            statement.setString(1, guildId);
            statement.setString(2, userId);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
        mutedUsersDirty.put(guildId, true);
    }

    public static List<String> getMutedUsers(String guildId, MessageReceivedEvent event) {
        if (lastRefresh.isBefore(LocalDateTime.now().minusMinutes(1))) {


            try (PreparedStatement statement = DatabaseConnector.getConn().
                    prepareStatement("SELECT * from shepard_func.get_muted_users()")) {
                ResultSet result = statement.executeQuery();

                Map<String, List<String>> data = new HashMap<>();

                while (result.next()) {
                    String guild = result.getString("guild_id");
                    String user = result.getString("user_id");

                    if (data.containsKey(guild)) {
                        data.get(guild).add(user);
                    } else {
                        data.put(guild, List.of(user));
                    }
                }

                mutedUsers = data;

            } catch (SQLException e) {
                handleException(e, event);
            }
            lastRefresh = LocalDateTime.now();

        } else {
            if (mutedUsers.containsKey(guildId)) {
                if (!mutedUsersDirty.get(guildId)) {
                    return mutedUsers.get(guildId);
                } else {
                    refreshGuildData(guildId, event);
                }
            }

        }


        return mutedUsers.getOrDefault(guildId, Collections.emptyList());
    }
}
