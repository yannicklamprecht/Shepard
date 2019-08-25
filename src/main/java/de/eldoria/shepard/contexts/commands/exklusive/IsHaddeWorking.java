package de.chojo.shepard.contexts.commands.exklusive;

import de.chojo.shepard.calendar.CalendarEvent;
import de.chojo.shepard.messagehandler.MessageSender;
import de.chojo.shepard.contexts.commands.Command;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static de.chojo.shepard.calendar.CalendarQuickstart.getHaddeWorktimes;

public class IsHaddeWorking extends Command {

    /**
     * Creates new hadde working command object.
     */
    public IsHaddeWorking() {
        commandName = "isHaddeWorking";
        commandAliases = new String[] {"istHaddeArbeiten"};
        commandDesc = "Gibt an, ob Hadde arbeiten ist";
    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        try {
            Calendar cal = Calendar.getInstance();
            Date currentDate = cal.getTime();

            CalendarEvent event = getHaddeWorktimes();
            List<MessageEmbed.Field> fields = new ArrayList<>();
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
                    fields.add(new MessageEmbed.Field("Ist Hadde arbeiten?", "Ja, bis "
                            + getTime.format(event.getEnd()) + " Uhr " + endDate, false));
                    MessageSender.sendTextBox(null, fields, receivedEvent.getChannel());
                } else {
                    fields.add(new MessageEmbed.Field("Ist Hadde arbeiten?", "Nein, er arbeitet "
                            + startDate + " von " + getTime.format(event.getStart()) + " Uhr bis "
                            + getTime.format(event.getEnd()) + " Uhr.", false));
                    MessageSender.sendTextBox(null, fields, receivedEvent.getChannel());
                }
            } else {
                fields.add(new MessageEmbed.Field("Ist Hadde arbeiten?",
                        "Ich hab derzeit leider keine Arbeitszeiten", false));
                MessageSender.sendTextBox(null, fields, receivedEvent.getChannel());

            }
        } catch (IOException | GeneralSecurityException e) {
            receivedEvent.getChannel().sendMessage("**Exception** occurred :confused:").queue();
        }

    }
}
