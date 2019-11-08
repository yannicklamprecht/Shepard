package de.eldoria.shepard.contexts.commands.exklusive;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.calendar.CalendarEvent;
import de.eldoria.shepard.contexts.commands.Command;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static de.eldoria.shepard.calendar.CalendarQuickstart.getEldoriaMeetings;

@Deprecated
public class Meetings extends Command {

    /**
     * Creates a new Meetings command object.
     */
    public Meetings() {
        commandName = "meetings";
        commandAliases = new String[] {"besprechung", "meeting"};
        commandDesc = "Der nächste Besprechungstermin";
        category = ContextCategory.EXCLUSIVE;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        try {
            List<CalendarEvent> calendarEvent = getEldoriaMeetings();
            SimpleDateFormat getTime = new SimpleDateFormat("HH:mm");
            SimpleDateFormat getDate = new SimpleDateFormat("dd.MM.yyyy");

            ArrayList<LocalizedField> fields = new ArrayList<>();
            fields.add(new LocalizedField("Nächste Meetings:", calendarEvent.get(0).getSummary()
                    + " am " + getDate.format(calendarEvent.get(0).getStart()) + " von "
                    + getTime.format(calendarEvent.get(0).getStart()) + " bis "
                    + getTime.format(calendarEvent.get(0).getEnd()) + System.lineSeparator()
                    + calendarEvent.get(1).getSummary() + " am " + getDate.format(calendarEvent.get(1).getStart())
                    + " von " + getTime.format(calendarEvent.get(1).getStart()) + " bis "
                    + getTime.format(calendarEvent.get(1).getEnd()), false, messageContext));
            MessageSender.sendTextBox(null, fields, messageContext.getTextChannel());
        } catch (IOException | GeneralSecurityException e) {
            messageContext.getChannel().sendMessage("**Exception** occurred :confused:").queue();
        }
    }
}

