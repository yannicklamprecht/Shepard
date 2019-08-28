package de.eldoria.shepard.database;

import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            return "";
        }
        return matcher.group(1);
    }

    /**
     * Handles SQL Exceptions.
     *
     * @param ex    SQL Exception
     * @param event Event for error sending to channel to inform user.
     */
    public static void handleException(SQLException ex, MessageReceivedEvent event) {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());

        if (event != null) {
            MessageSender.sendSimpleError("Ups. Looks like my Database has a small hickup."
                    + System.lineSeparator()
                    + "Can you give me another try, pls?", event.getChannel());
        }
    }



}
