package de.eldoria.shepard.database;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.lineSeparator;

public final class DbUtil {
    private static final Pattern ID_PATTERN = Pattern.compile("(?:<[@#!&]{1,2})?(?<id>[0-9]{18})(?:>)?");

    private DbUtil() {
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
    public static void handleException(SQLException ex, MessageEventDataWrapper event) throws SQLException {
        StringBuilder builder = new StringBuilder();

        builder.append("SQLException: ").append(ex.getMessage()).append(lineSeparator())
                .append("SQLState: ").append(ex.getSQLState()).append(lineSeparator())
                .append("VendorError: ").append(ex.getErrorCode());
        ShepardBot.getLogger().error(builder.toString());

        if (event != null) {
            MessageSender.sendSimpleError(ErrorType.DATABASE_ERROR, event.getChannel());
        }
        MessageSender.sendSimpleErrorEmbed(builder.toString(), Normandy.getErrorChannel());
        throw ex;
    }

    /**
     * Handles SQL Exceptions.
     *  @param ex    SQL Exception
     * @param event Event for error sending to channel to inform user.
     */
    public static void handleExceptionAndIgnore(SQLException ex, MessageEventDataWrapper event) {
        try {
            handleException(ex, event);
        } catch (SQLException e) {
            //DO NOTHING
        }
    }

}
