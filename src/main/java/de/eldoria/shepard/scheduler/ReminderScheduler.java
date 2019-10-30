package de.eldoria.shepard.scheduler;

import de.eldoria.shepard.database.queries.ReminderData;
import de.eldoria.shepard.database.types.ReminderComplex;
import de.eldoria.shepard.localization.LanguageHandler;
import de.eldoria.shepard.localization.enums.scheduler.ReminderLocale;
import de.eldoria.shepard.messagehandler.MessageSender;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderScheduler implements Runnable {
    public void initialize() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        List<ReminderComplex> expiredReminder = ReminderData.getAndDeleteExpiredReminder(null);
        for (ReminderComplex reminder : expiredReminder) {
            if (reminder.getUser() == null) {
                return;
            }

            if (reminder.getChannel() == null
                    || reminder.getGuild() == null
                    || reminder.getGuild().getMember(reminder.getUser()) == null) {
                reminder.getUser().openPrivateChannel().queue(privateChannel ->
                        MessageSender.sendMessageToChannel("I should remind you of something... Let me think... AH!"
                                + System.lineSeparator() + "**" + reminder.getText() + "**", privateChannel));
                return;
            }

            LanguageHandler locale = LanguageHandler.getInstance();
            MessageSender.sendMessage(locale.getReplacedString(ReminderLocale.M_REMINDER.localeCode,
                    reminder.getGuild(), reminder.getUser().getAsMention()) + System.lineSeparator()
                    + "**" + reminder.getText() + "**", reminder.getChannel());
        }
    }
}
