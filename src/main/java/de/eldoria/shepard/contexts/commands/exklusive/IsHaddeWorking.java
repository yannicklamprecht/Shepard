package de.eldoria.shepard.contexts.commands.exklusive;

import de.eldoria.shepard.calendar.CalendarEvent;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static de.eldoria.shepard.calendar.CalendarQuickstart.getHaddeWorktimes;

@Deprecated
public class IsHaddeWorking extends Command {

    /**
     * Creates new hadde working command object.
     */
    public IsHaddeWorking() {
        commandName = "isHaddeWorking";
        commandAliases = new String[] {"istHaddeArbeiten"};
        commandDesc = "Gibt an, ob Hadde arbeiten ist";
        category = ContextCategory.EXCLUSIVE;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        try {
            Calendar cal = Calendar.getInstance();
            Date currentDate = cal.getTime();

            CalendarEvent event = getHaddeWorktimes();
            List<LocalizedField> fields = new ArrayList<>();
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
                    fields.add(new LocalizedField("Ist Hadde arbeiten?", "Ja, bis "
                            + getTime.format(event.getEnd()) + " Uhr " + endDate, false, messageContext));
                    MessageSender.sendTextBox(null, fields, messageContext.getTextChannel());
                } else {
                    fields.add(new LocalizedField("Ist Hadde arbeiten?", "Nein, er arbeitet "
                            + startDate + " von " + getTime.format(event.getStart()) + " Uhr bis "
                            + getTime.format(event.getEnd()) + " Uhr.", false, messageContext));
                    MessageSender.sendTextBox(null, fields, messageContext.getTextChannel());
                }
            } else {
                fields.add(new LocalizedField("Ist Hadde arbeiten?",
                        "Ich hab derzeit leider keine Arbeitszeiten", false, messageContext));
                MessageSender.sendTextBox(null, fields, messageContext.getTextChannel());

            }
        } catch (IOException | GeneralSecurityException e) {
            messageContext.getChannel().sendMessage("**Exception** occurred :confused:").queue();
        }

    }
}
