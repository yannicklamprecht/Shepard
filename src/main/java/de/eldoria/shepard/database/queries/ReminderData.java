package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.types.ReminderComplex;
import de.eldoria.shepard.database.types.ReminderSimple;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.eldoria.shepard.database.DatabaseConnector.getConn;
import static de.eldoria.shepard.database.DbUtil.handleExceptionAndIgnore;

public final class ReminderData {
    private ReminderData() {
    }

    public static boolean addReminderInterval(Guild guild, User user, TextChannel channel, String message, String interval,
                                              MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = getConn()
                .prepareStatement("SELECT shepard_func.add_reminder_interval(?,?,?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            statement.setString(3, channel.getId());
            statement.setString(4, message);
            statement.setString(5, interval);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Add a reminder on a date on a specific time
     *
     * @param guild          guild on which the reminder was created
     * @param user           user which created the reminder
     * @param channel        channel in which the reminder was created
     * @param message        message for reminder
     * @param date           date when the reminder should be posted
     * @param time           time when the reminder should be posted (Format: hh24:mm
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean addReminderDate(Guild guild, User user, TextChannel channel, String message, String date, String time,
                                          MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = getConn()
                .prepareStatement("SELECT shepard_func.add_reminder_date(?,?,?,?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            statement.setString(3, channel.getId());
            statement.setString(4, message);
            statement.setString(5, date);
            statement.setString(6, time);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }


    public static List<ReminderSimple> getUserReminder(Guild guild, User user, MessageEventDataWrapper messageContext) {
        List<ReminderSimple> result;
        try (PreparedStatement statement = getConn()
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
            handleExceptionAndIgnore(e, messageContext);
            return Collections.emptyList();
        }
        return result;
    }

    public static boolean removeUserReminder(Guild guild, User user, int id, MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = getConn()
                .prepareStatement("SELECT * from shepard_func.remove_reminder(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            statement.setInt(3, id);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    public static List<ReminderComplex> getAndDeleteExpiredReminder(MessageEventDataWrapper messageContext) {
        List<ReminderComplex> result;
        try (PreparedStatement statement = getConn()
                .prepareStatement("SELECT * from shepard_func.get_expired_reminder()")) {
            ResultSet resultSet = statement.executeQuery();
            result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(
                        new ReminderComplex(
                                resultSet.getString("guild_id"),
                                resultSet.getString("channel_id"),
                                resultSet.getString("user_id"),
                                resultSet.getString("message")
                        ));
            }
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return Collections.emptyList();
        }

        try (PreparedStatement statement = getConn()
                .prepareStatement("SELECT * from shepard_func.remove_expired_reminder()")) {
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return Collections.emptyList();
        }

        return result;
    }

}
