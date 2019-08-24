package de.chojo.shepard.database.queries;

import de.chojo.shepard.database.DatabaseConnector;
import net.dv8tion.jda.api.entities.Guild;
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

public final class Mutes {

    private static Map<String, List<String>> mutedUsers = new HashMap<>();
    private static final Map<String, Boolean> mutedUsersDirty = new HashMap<>();
    private static LocalDateTime lastRefresh;

    private Mutes() {
    }

    /**
     * Sets a user as muted.
     *
     * @param guild    Guild on which the user should be muted
     * @param userId   user id
     * @param duration duration of the mute
     * @param event    event from command sending for error handling. Can be null.
     */
    public static void setMuted(Guild guild, String userId, String duration, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_muted(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, userId);
            statement.setString(3, duration);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }

        mutedUsersDirty.put(guild.getId(), true);
    }

    private static List<String> refreshGuildData(Guild guild, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.get_muted_users(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.getArray(1) != null) {

                mutedUsers.put(guild.getId(), Arrays.asList((String[]) result.getArray(1).getArray()));
            }
        } catch (SQLException e) {
            handleException(e, event);
        }
        return Collections.emptyList();
    }

    /**
     * Remove a mute from a user.
     *
     * @param guild  Guild object for lookup
     * @param userId id of the user
     * @param event  event from command sending for error handling. Can be null.
     */
    public static void removeMute(Guild guild, String userId, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_mute(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, userId);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
        mutedUsersDirty.put(guild.getId(), true);
    }

    /**
     * Get the muted users on a guild.
     *
     * @param guild Guild object for lookup
     * @param event event from command sending for error handling. Can be null.
     * @return List of muted users on a server.
     */
    public static List<String> getMutedUsers(Guild guild, MessageReceivedEvent event) {
        if (lastRefresh.isBefore(LocalDateTime.now().minusMinutes(1))) {


            try (PreparedStatement statement = DatabaseConnector.getConn()
                    .prepareStatement("SELECT * from shepard_func.get_muted_users()")) {
                ResultSet result = statement.executeQuery();

                Map<String, List<String>> data = new HashMap<>();

                while (result.next()) {
                    String user = result.getString("user_id");

                    if (data.containsKey(guild)) {
                        data.get(guild.getId()).add(user);
                    } else {
                        data.put(guild.getId(), List.of(user));
                    }
                }

                mutedUsers = data;

            } catch (SQLException e) {
                handleException(e, event);
            }
            lastRefresh = LocalDateTime.now();

        } else {
            if (mutedUsers.containsKey(guild.getId())) {
                if (!mutedUsersDirty.get(guild.getId())) {
                    return mutedUsers.get(guild.getId());
                } else {
                    refreshGuildData(guild, event);
                }
            }

        }
        return mutedUsers.getOrDefault(guild.getId(), Collections.emptyList());
    }
}
