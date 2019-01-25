package de.chojo.shepard.modules.commands.exklusive;

import de.chojo.shepard.util.ListType;
import de.chojo.shepard.Messages;
import de.chojo.shepard.calendar.CalendarEvent;
import de.chojo.shepard.modules.commands.Command;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static de.chojo.shepard.calendar.CalendarQuickstart.getEldoriaMeetings;

public class Meetings extends Command {

    public Meetings() {
        commandName = "meetings";
        commandAliases = new String[]{"besprechung", "meeting"};
        commandDesc = "Der nächste Besprechungstermin";
        args = null;
        serverListType = ListType.Whitelist;
        listedServer = new String[]{"214352508594814976"};
        serverCheckEnabled = true;
    }

    @Override
    public boolean execute(String[] args, MessageChannel channel, MessageReceivedEvent receivedEvent) {
        try {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            Date currentDate = cal.getTime();

            ArrayList<CalendarEvent> calendarEvent = getEldoriaMeetings();
            SimpleDateFormat getTime = new SimpleDateFormat("HH:mm");
            SimpleDateFormat getDate = new SimpleDateFormat("dd.MM.yyyy");

            ArrayList<MessageEmbed.Field> fields = new ArrayList<>();
            fields.add(new MessageEmbed.Field("Nächste Meetings:", calendarEvent.get(0).getSummary() + " am " + getDate.format(calendarEvent.get(0).getStart()) + " von " + getTime.format(calendarEvent.get(0).getStart()) + " bis " + getTime.format(calendarEvent.get(0).getEnd()) + System.lineSeparator() +
                    calendarEvent.get(1).getSummary() + " am " + getDate.format(calendarEvent.get(1).getStart()) + " von " + getTime.format(calendarEvent.get(1).getStart()) + " bis " + getTime.format(calendarEvent.get(1).getEnd()), false));
            Messages.sendTextBox(null, fields, channel);
        } catch (IOException | GeneralSecurityException e) {
            channel.sendMessage("**Exception** occurred :confused:").queue();
        }

        return true;
    }
}

