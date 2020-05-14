package de.eldoria.shepard.commandmodules.reminder.types;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class ReminderSimple {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final long reminderId;
    private final String text;
    private final Instant time;
    private final String timeString;

    /**
     * Creates a new reminder with timestamp.
     *
     * @param reminderId reminder id.
     * @param text       reminder text
     * @param timestamp  reminder timestamp.
     */
    public ReminderSimple(Long reminderId, String text, Timestamp timestamp) {
        this.reminderId = reminderId;
        this.time = timestamp.toInstant();
        this.text = text;
        this.timeString = dtf.format(timestamp.toLocalDateTime());
    }


    /**
     * Get the reminder id as a hex string. This takes precedence.
     *
     * @return reminder id
     */
    public String getReminderId() {
        return Long.toString(reminderId, 16);
    }

    /**
     * @return
     */
    public long getReminderIdLong() {
        return reminderId;
    }

    /**
     * Get the text of the reminder.
     *
     * @return reminder text
     */
    public String getText() {
        return text;
    }

    /**
     * Get the time of the reminder.
     *
     * @return reminder time
     */
    public String getTimeString() {
        return timeString;
    }
}
