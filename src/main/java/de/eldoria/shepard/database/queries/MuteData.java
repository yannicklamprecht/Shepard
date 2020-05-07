package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.eldoria.shepard.database.DbUtil.handleException;

public final class MuteData extends QueryObject {

    private static final Map<String, Boolean> mutedUsersDirty = new HashMap<>();
    private static Map<String, List<String>> mutedUsers = new HashMap<>();
    private static LocalDateTime lastRefresh;

    /**
     * Create a new mute data object.
     *
     * @param source data source for information retrieval
     */
    public MuteData(DataSource source) {
        super(source);
    }

    /**
     * Sets a user as muted.
     *
     * @param guild          Guild on which the user should be muted
     * @param user           user id
     * @param duration       duration of the mute
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setMuted(Guild guild, User user, String duration, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.set_muted(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            statement.setString(3, duration);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }

        mutedUsersDirty.put(guild.getId(), true);
        return true;
    }

    private void refreshGuildData(Guild guild, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.get_muted_users(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.getArray(1) != null) {

                mutedUsers.put(guild.getId(), Arrays.asList((String[]) result.getArray(1).getArray()));
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
    }

    /**
     * Remove a mute from a user.
     *
     * @param guild          Guild object for lookup
     * @param user           id of the user
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean removeMute(Guild guild, User user, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.remove_mute(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        mutedUsersDirty.put(guild.getId(), true);
        return true;
    }

    /**
     * Get the muted users on a guild.
     *
     * @param guild          Guild object for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return List of muted users on a server.
     */
    public List<String> getMutedUsers(Guild guild, MessageEventDataWrapper messageContext) {
        if (lastRefresh.isBefore(LocalDateTime.now().minusMinutes(1))) {


            try (var conn = source.getConnection(); PreparedStatement statement = conn
                    .prepareStatement("SELECT * from shepard_func.get_muted_users()")) {
                ResultSet result = statement.executeQuery();

                Map<String, List<String>> data = new HashMap<>();

                while (result.next()) {
                    String user = result.getString("user_id");

                    if (data.containsKey(guild.getId())) {
                        data.get(guild.getId()).add(user);
                    } else {
                        data.put(guild.getId(), List.of(user));
                    }
                }

                mutedUsers = data;

            } catch (SQLException e) {
                handleException(e, messageContext);
            }
            lastRefresh = LocalDateTime.now();

        } else {
            if (mutedUsers.containsKey(guild.getId())) {
                if (!mutedUsersDirty.get(guild.getId())) {
                    return mutedUsers.get(guild.getId());
                } else {
                    refreshGuildData(guild, messageContext);
                }
            }

        }
        return mutedUsers.getOrDefault(guild.getId(), Collections.emptyList());
    }
}
