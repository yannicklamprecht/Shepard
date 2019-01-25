package de.chojo.shepard.modules.commands.exklusive;

import de.chojo.shepard.calendar.CalendarEvent;
import de.chojo.shepard.Messages;
import de.chojo.shepard.modules.commands.Command;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static de.chojo.shepard.calendar.CalendarQuickstart.getHaddeWorktimes;

public class IsHaddeWorking extends Command {

    public IsHaddeWorking() {
        commandName = "isHaddeWorking";
        commandAliases = new String[]{"istHaddeArbeiten"};
        commandDesc = "Gibt an, ob Hadde arbeiten ist";
        args = null;
    }


    @Override
    public boolean execute(String[] args, MessageChannel channel, MessageReceivedEvent receivedEvent) {
        try {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            Date currentDate = cal.getTime();

            CalendarEvent event = getHaddeWorktimes();
            ArrayList<MessageEmbed.Field> fields = new ArrayList<>();
            if (event != null) {

                SimpleDateFormat getTime = new SimpleDateFormat("HH:mm");
                SimpleDateFormat getDate = new SimpleDateFormat("dd.MM.yyyy");


                String startDate = "wieder am " + getDate.format(event.getStart());
                String endDate = "am " + getDate.format(event.getEnd());
                if (event.getStart().getDate() == currentDate.getDate()) {
                    startDate = "heute";
                }
                if (event.getEnd().getDate() == currentDate.getDate()) {
                    endDate = "heute";
                }

                if (currentDate.after(event.getStart()) && currentDate.before(event.getEnd())) {
                    fields.add(new MessageEmbed.Field("Ist Hadde arbeiten?", "Ja, bis " + getTime.format(event.getEnd()) + " Uhr " + endDate, false));
                    Messages.sendTextBox(null, fields, channel);
                } else {
                    fields.add(new MessageEmbed.Field("Ist Hadde arbeiten?", "Nein, er arbeitet " + startDate + " von " + getTime.format(event.getStart()) + " Uhr bis " + getTime.format(event.getEnd()) + " Uhr.", false));
                    Messages.sendTextBox(null, fields, channel);
                }
            } else {
                fields.add(new MessageEmbed.Field("Ist Hadde arbeiten?","Ich hab derzeit leider keine Arbeitszeiten", false));
                Messages.sendTextBox(null, fields, channel);

            }
        } catch (IOException | GeneralSecurityException e) {
            channel.sendMessage("**Exception** occurred :confused:").queue();
        }

        return true;
    }
}
