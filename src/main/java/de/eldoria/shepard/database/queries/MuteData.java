package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.listener.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.eldoria.shepard.database.DbUtil.handleExceptionAndIgnore;

public final class MuteData {

    private static Map<String, List<String>> mutedUsers = new HashMap<>();
    private static final Map<String, Boolean> mutedUsersDirty = new HashMap<>();
    private static LocalDateTime lastRefresh;

    private MuteData() {
    }

    /**
     * Sets a user as muted.
     *
     * @param guild    Guild on which the user should be muted
     * @param user     user id
     * @param duration duration of the mute
     * @param event    event from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean setMuted(Guild guild, User user, String duration, MessageEventDataWrapper event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_muted(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            statement.setString(3, duration);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, event);
            return false;
        }

        mutedUsersDirty.put(guild.getId(), true);
        return true;
    }

    private static boolean refreshGuildData(Guild guild, MessageEventDataWrapper event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.get_muted_users(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.getArray(1) != null) {

                mutedUsers.put(guild.getId(), Arrays.asList((String[]) result.getArray(1).getArray()));
            }
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, event);
            return false;
        }
        return true;
    }

    /**
     * Remove a mute from a user.
     *
     * @param guild Guild object for lookup
     * @param user  id of the user
     * @param event event from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean removeMute(Guild guild, User user, MessageEventDataWrapper event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_mute(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, event);
            return false;
        }
        mutedUsersDirty.put(guild.getId(), true);
        return true;
    }

    /**
     * Get the muted users on a guild.
     *
     * @param guild Guild object for lookup
     * @param event event from command sending for error handling. Can be null.
     * @return List of muted users on a server.
     */
    public static List<String> getMutedUsers(Guild guild, MessageEventDataWrapper event) {
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
                handleExceptionAndIgnore(e, event);
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
