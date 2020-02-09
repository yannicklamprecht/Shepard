package de.eldoria.shepard.database;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.types.Rank;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.lineSeparator;

@Slf4j
public final class DbUtil {
    private static final Pattern ID_PATTERN = Pattern.compile("(?:<[@#!&]{1,2})?(?<id>[0-9]{18})(?:>)?");

    private DbUtil() {
    }

    /**
     * Get a sorted ranked list from a result set.
     *
     * @param result Result set to retrieve ranks.
     * @return List of ranks.
     * @throws SQLException SQL exception
     */
    public static List<Rank> getScoreListFromResult(ResultSet result) throws SQLException {
        List<Rank> ranks = new ArrayList<>();

        while (result.next()) {
            User user = ShepardBot.getJDA().getUserById(result.getString("user_id"));
            if (user != null) {
                ranks.add(new Rank(user, result.getInt("score")));
            }
        }
        return ranks;
    }


    /**
     * Extracts an id from discord's formatting.
     *
     * @param id the formatted id.
     * @return the extracted id.
     */
    public static String getIdRaw(String id) {
        Matcher matcher = ID_PATTERN.matcher(id);
        if (!matcher.matches()) {
            return "0";
        }
        return matcher.group(1);
    }

    /**
     * Handles SQL Exceptions and throws it.
     *
     * @param ex    SQL Exception
     * @param event Event for error sending to channel to inform user.
     * @throws SQLException when the query was not executed successful
     */
    private static void handleException(SQLException ex, MessageEventDataWrapper event) throws SQLException {
        StringBuilder builder = new StringBuilder();

        builder.append("SQLException: ").append(ex.getMessage()).append(lineSeparator())
                .append("SQLState: ").append(ex.getSQLState()).append(lineSeparator())
                .append("VendorError: ").append(ex.getErrorCode());
        log.error(builder.toString(), ex);

        if (event != null) {
            MessageSender.sendSimpleError(ErrorType.DATABASE_ERROR, event.getTextChannel());
        }
        throw ex;
    }

    /**
     * Handles SQL Exceptions.
     *
     * @param ex    SQL Exception
     * @param event Event for error sending to channel to inform user.
     */
    public static void handleExceptionAndIgnore(SQLException ex, MessageEventDataWrapper event) {
        try {
            handleException(ex, event);
        } catch (SQLException e) {
            log.error("failed to handle exception", e);
        }
    }

}
