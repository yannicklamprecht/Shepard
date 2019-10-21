package de.eldoria.shepard.database.types;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class ReminderSimple {
    private final int reminderId;
    private final String text;
    private final String time;

    public ReminderSimple(int reminderId, String text, Timestamp timestamp) {
        this.reminderId = reminderId;
        this.text = text;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        this.time = dtf.format(timestamp.toLocalDateTime());
    }

    public ReminderSimple(int reminderId, String text) {
        this.reminderId = reminderId;
        this.text = text;
        this.time = "";
    }

    public int getReminderId() {
        return reminderId;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }
}
