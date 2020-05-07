package de.eldoria.shepard.commandmodules.reminder.data;

import de.eldoria.shepard.commandmodules.reminder.types.ReminderComplex;
import de.eldoria.shepard.commandmodules.reminder.types.ReminderSimple;
import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.eldoria.shepard.database.DbUtil.handleException;

public final class ReminderData extends QueryObject {

    /**
     * Create a new reminder data object.
     * @param source data source for connection retrieval
     */
    public ReminderData(DataSource source) {
        super(source);
    }

    /**
     * Add a new reminder in a interval.
     *
     * @param guild          guild for saving
     * @param user           user which created the invite
     * @param channel        channel where the invite was created
     * @param message        message for reminder
     * @param interval       reminder interval
     * @param messageContext message context for error handling. can be null
     * @return true if the query was executed successfully
     */
    public boolean addReminderInterval(Guild guild, User user, TextChannel channel,
                                       String message, String interval,
                                       MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.add_reminder_interval(?,?,?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            statement.setString(3, channel.getId());
            statement.setString(4, message);
            statement.setString(5, interval);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Add a reminder on a date on a specific time.
     *
     * @param guild          guild on which the reminder was created
     * @param user           user which created the reminder
     * @param channel        channel in which the reminder was created
     * @param message        message for reminder
     * @param date           date when the reminder should be posted
     * @param time           time when the reminder should be posted (Format: hh24:mm
     * @param messageContext message context for error handling. can be null
     * @return true if the query execution was successful
     */
    public boolean addReminderDate(Guild guild, User user, TextChannel channel,
                                   String message, String date, String time,
                                   MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.add_reminder_date(?,?,?,?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            statement.setString(3, channel.getId());
            statement.setString(4, message);
            statement.setString(5, date);
            statement.setString(6, time);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Get the reminder of the user on a guild.
     *
     * @param guild          guild of the user.
     * @param user           user
     * @param messageContext message context for error handling. can be null
     * @return list of reminder of the user on the guild
     */
    public List<ReminderSimple> getUserReminder(Guild guild, User user, MessageEventDataWrapper messageContext) {
        List<ReminderSimple> result;
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_user_reminder(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            ResultSet resultSet = statement.executeQuery();
            result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(
                        new ReminderSimple(
                                resultSet.getInt("reminder_id"),
                                resultSet.getString("message"),
                                resultSet.getTimestamp("reminder_time")
                        ));
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
            return Collections.emptyList();
        }
        return result;
    }

    /**
     * Remove a reminder of a user by id.
     *
     * @param guild          guild in which the reminder should be deleted.
     * @param user           user which created the invite
     * @param id             id of the reminder
     * @param messageContext message context for error handling. can be null
     * @return true if the query was executed successfully
     */
    public boolean removeUserReminder(Guild guild, User user, int id, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.remove_reminder(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            statement.setInt(3, id);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Get the expired reminders and delete them on database.
     *
     * @param jda jda instance
     * @param messageContext message context for error handling. can be null
     * @return list of expired reminder
     */
    public List<ReminderComplex> getAndDeleteExpiredReminder(JDA jda, MessageEventDataWrapper messageContext) {
        List<ReminderComplex> result;
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_expired_reminder()")) {
            ResultSet resultSet = statement.executeQuery();
            result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(
                        new ReminderComplex(jda,
                                resultSet.getString("guild_id"),
                                resultSet.getString("channel_id"),
                                resultSet.getString("user_id"),
                                resultSet.getString("message")
                        ));
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
            return Collections.emptyList();
        }

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.remove_expired_reminder()")) {
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return Collections.emptyList();
        }

        return result;
    }
}
