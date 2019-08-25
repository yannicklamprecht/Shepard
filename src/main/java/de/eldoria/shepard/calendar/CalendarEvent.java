package de.chojo.shepard.calendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

import java.util.Date;

public class CalendarEvent {
    private final Date start;
    private final Date end;
    private final String summary;

    /**
     * Creates a new calendar event.
     *
     * @param event Google calendar api event
     */
    CalendarEvent(Event event) {
        start = getDate(event.getStart().getDateTime());
        end = getDate(event.getEnd().getDateTime());
        summary = event.getSummary();
    }

    /**
     * Get the date of the event.
     *
     * @param time Date Time object
     * @return Date from Date Time object
     */
    private Date getDate(DateTime time) {
        return new Date(time.getValue());
    }

    /**
     * Start date of the event.
     *
     * @return Date object
     */
    public Date getStart() {
        return start;
    }

    /**
     * End date of the event.
     *
     * @return Date object
     */
    public Date getEnd() {
        return end;
    }

    /**
     * Summary of the event.
     *
     * @return summary as string
     */
    public String getSummary() {
        return summary;
    }
}
