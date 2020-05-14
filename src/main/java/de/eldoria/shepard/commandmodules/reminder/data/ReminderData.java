package de.eldoria.shepard.commandmodules.reminder.data;

import de.eldoria.shepard.commandmodules.reminder.types.ReminderComplex;
import de.eldoria.shepard.commandmodules.reminder.types.ReminderSimple;
import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static de.eldoria.shepard.database.DbUtil.handleException;

public final class ReminderData extends QueryObject {
    /**
     * Create a new reminder data object.
     *
     * @param source data source for connection retrieval
     */
    public ReminderData(DataSource source) {
        super(source);
    }

    /**
     * Add a new reminder in a interval.
     *
     * @param message        message for reminder
     * @param interval       reminder interval
     * @param messageContext message context for error handling. can be null
     * @return id of the reminder as hexadecimal or or null if the creation failed
     */
    public Optional<String> addReminderInterval(String message, String interval,
                                                EventWrapper messageContext) {
        var guildId = 0L;

        if (messageContext.isGuildEvent()) {
            guildId = messageContext.getGuild().get().getIdLong();
        }

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.add_reminder_interval(?,?,?,?,?)")) {
            statement.setLong(1, guildId);
            statement.setLong(2, messageContext.getAuthor().getIdLong());
            statement.setLong(3, messageContext.getMessageChannel().getIdLong());
            statement.setString(4, message);
            statement.setString(5, interval);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(Long.toHexString(resultSet.getLong(1)));
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return Optional.empty();
    }

    /**
     * Add a reminder on a date on a specific time.
     *
     * @param message        message for reminder
     * @param date           date when the reminder should be posted
     * @param time           time when the reminder should be posted (Format: hh24:mm
     * @param messageContext message context for error handling. can be null
     * @return id of the reminder as hexadecimal or or null if the creation failed
     */
    public Optional<String> addReminderDate(String message, String date, String time,
                                            EventWrapper messageContext) {
        var guildId = 0L;

        if (messageContext.isGuildEvent()) {
            guildId = messageContext.getGuild().get().getIdLong();
        }

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.add_reminder_date(?,?,?,?,?,?)")) {
            statement.setLong(1, guildId);
            statement.setLong(2, messageContext.getAuthor().getIdLong());
            statement.setLong(3, messageContext.getMessageChannel().getIdLong());
            statement.setString(4, message);
            statement.setString(5, date);
            statement.setString(6, time);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(Long.toHexString(resultSet.getLong(1)));
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return Optional.empty();
    }

    /**
     * Get the reminder of the user on a guild.
     *
     * @param messageContext message context for error handling. can be null
     * @return list of reminder of the user on the guild
     */
    public List<ReminderSimple> getUserReminder(EventWrapper messageContext) {
        List<ReminderSimple> result;
        var guildId = 0L;

        if (messageContext.isGuildEvent()) {
            guildId = messageContext.getGuild().get().getIdLong();
        }

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_user_reminder(?,?)")) {
            statement.setLong(1, guildId);
            statement.setLong(2, messageContext.getAuthor().getIdLong());
            ResultSet resultSet = statement.executeQuery();
            result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(
                        new ReminderSimple(
                                resultSet.getLong("reminder_id"),
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
     * Get the reminder of the user on a guild.
     *
     * @param wrapper message context for error handling. can be null
     * @return list of reminder of the user on the guild
     */
    public Optional<ReminderSimple> getReminder(long id, EventWrapper wrapper) {
        var guildId = 0L;

        if (wrapper.isGuildEvent()) {
            guildId = wrapper.getGuild().get().getIdLong();
        }

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_reminder(?,?,?)")) {
            statement.setLong(1, guildId);
            statement.setLong(2, wrapper.getAuthor().getIdLong());
            statement.setLong(3, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return
                        Optional.of(new ReminderComplex(wrapper.getJDA().getShardManager(),
                                resultSet.getLong("id"),
                                resultSet.getLong("guild_id"),
                                resultSet.getLong("channel_id"),
                                resultSet.getLong("user_id"),
                                resultSet.getString("message"),
                                resultSet.getTimestamp("reminder_time").toInstant(),
                                resultSet.getInt("snooze_count")
                        ));
            }
        } catch (SQLException e) {
            handleException(e, wrapper);
        }
        return Optional.empty();
    }

    /**
     * Remove a reminder of a user by id.
     *
     * @param id             id of the reminder
     * @param messageContext message context for error handling. can be null
     * @return true if the query was executed successfully
     */
    public boolean removeUserReminder(long id, EventWrapper messageContext) {
        var guildId = 0L;

        if (messageContext.isGuildEvent()) {
            guildId = messageContext.getGuild().get().getIdLong();
        }

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.remove_reminder(?,?,?)")) {
            statement.setLong(1, guildId);
            statement.setLong(2, messageContext.getAuthor().getIdLong());
            statement.setLong(3, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }

        return false;
    }

    /**
     * Remove a reminder of a user by id.
     *
     * @param id             id of the reminder
     * @param messageContext message context for error handling. can be null
     * @return true if the query was executed successfully
     */
    public Instant snoozeReminder(long id, String interval, EventWrapper messageContext) {
        var guildId = 0L;

        if (messageContext.isGuildEvent()) {
            guildId = messageContext.getGuild().get().getIdLong();
        }

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.snooze_reminder(?,?,?,?)")) {
            statement.setLong(1, guildId);
            statement.setLong(2, messageContext.getAuthor().getIdLong());
            statement.setLong(3, id);
            statement.setString(4, interval);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getTimestamp(1).toInstant();
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
            return null;
        }

        return null;
    }

    /**
     * Restore a reminder of a user by id.
     *
     * @param id             id of the reminder
     * @param messageContext message context for error handling. can be null
     * @return true if the query was executed successfully
     */
    public boolean restoreReminder(long id, EventWrapper messageContext) {
        var guildId = 0L;

        if (messageContext.isGuildEvent()) {
            guildId = messageContext.getGuild().get().getIdLong();
        }

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.restore_reminder(?,?,?)")) {
            statement.setLong(1, guildId);
            statement.setLong(2, messageContext.getAuthor().getIdLong());
            statement.setLong(3, id);
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
     * @param shardManager   shardManager instance
     * @param messageContext message context for error handling. can be null
     * @return list of expired reminder
     */
    public List<ReminderComplex> getAndDeleteExpiredReminder(ShardManager shardManager, EventWrapper messageContext) {
        List<ReminderComplex> result;
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_expired_reminder()")) {
            ResultSet resultSet = statement.executeQuery();
            result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(
                        new ReminderComplex(shardManager,
                                resultSet.getLong("id"),
                                resultSet.getLong("guild_id"),
                                resultSet.getLong("channel_id"),
                                resultSet.getLong("user_id"),
                                resultSet.getString("message"),
                                resultSet.getInt("snooze_count")
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
