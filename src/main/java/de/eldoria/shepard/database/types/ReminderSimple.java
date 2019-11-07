package de.eldoria.shepard.database.types;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class ReminderSimple {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final int reminderId;
    private final String text;
    private final String time;

    /**
     * Creates a new reminder with timestamp
     * @param reminderId reminder id.
     * @param text reminder text
     * @param timestamp reminder timestamp.
     */
    public ReminderSimple(int reminderId, String text, Timestamp timestamp) {
        this.reminderId = reminderId;
        this.text = text;
        this.time = dtf.format(timestamp.toLocalDateTime());
    }

    /**
     * Creates a new reminder without timestamp.
     * @param reminderId id of reminder
     * @param text text of reminder.
     */
    public ReminderSimple(int reminderId, String text) {
        this.reminderId = reminderId;
        this.text = text;
        this.time = "";
    }

    /**
     * Get the reminder id.
     * @return reminder id
     */
    public int getReminderId() {
        return reminderId;
    }

    /**
     * Get the text of the reminder.
     * @return reminder text
     */
    public String getText() {
        return text;
    }

    /**
     * Get the time of the reminder.
     * @return reminder time
     */
    public String getTime() {
        return time;
    }
}
