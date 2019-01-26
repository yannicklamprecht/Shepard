package de.chojo.shepard.calendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

import java.util.Date;

public class CalendarEvent {
    private Date start;
    private Date end;
    private String summary;

    CalendarEvent(Event event) {
        start = getDate(event.getStart().getDateTime());
        end = getDate(event.getEnd().getDateTime());
        summary = event.getSummary();
    }

    private Date getDate(DateTime time){
        return new Date(time.getValue());
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public String getSummary() {
        return summary;
    }
}
