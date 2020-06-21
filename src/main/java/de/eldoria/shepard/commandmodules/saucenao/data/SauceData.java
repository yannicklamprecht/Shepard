package de.eldoria.shepard.commandmodules.saucenao.data;

import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.wrapper.EventWrapper;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static de.eldoria.shepard.database.DbUtil.handleException;

public class SauceData extends QueryObject {
    /**
     * Default constructor for data object.
     *
     * @param source data source for information retrieval
     */
    public SauceData(DataSource source) {
        super(source);
    }

    /**
     * Log that a sauce request was send.
     *
     * @param messageContext context for lookup
     * @return true if the transaction was succesful
     */
    public boolean logSauceRequest(EventWrapper messageContext) {
        var guildId = 0L;

        if (messageContext.isGuildEvent()) {
            guildId = messageContext.getGuild().get().getIdLong();
        }

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.add_sauce_request(?,?)")) {
            statement.setLong(1, guildId);
            statement.setLong(2, messageContext.getAuthor().getIdLong());
            statement.execute();
            return true;
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return true;
    }

    /**
     * Get the current request count
     *
     * @param messageContext message context for request lookup
     * @param longSeconds    seconds of long period
     * @param shortSeconds   seconds of short period
     * @return SauceRequests object or null if database is not reachable
     */
    public SauceRequests getSauceRequests(int longSeconds, int shortSeconds, EventWrapper messageContext) {
        var guildId = 0L;

        if (messageContext.isGuildEvent()) {
            guildId = messageContext.getGuild().get().getIdLong();
        }

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_sauce_requests(?,?,?,?)")) {
            statement.setLong(1, guildId);
            statement.setLong(2, messageContext.getAuthor().getIdLong());
            statement.setInt(3, longSeconds);
            statement.setInt(4, shortSeconds);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new SauceRequests(
                        result.getInt("total_long"),
                        result.getInt("wait_total_long"),
                        result.getInt("total_short"),
                        result.getInt("wait_total_short"),
                        result.getInt("user_long"),
                        result.getInt("wait_user_long"),
                        result.getInt("user_short"),
                        result.getInt("wait_user_short"),
                        result.getInt("guild_long"),
                        result.getInt("wait_guild_long"),
                        result.getInt("guild_short"),
                        result.getInt("wait_guild_short")
                );
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return null;
    }

}
