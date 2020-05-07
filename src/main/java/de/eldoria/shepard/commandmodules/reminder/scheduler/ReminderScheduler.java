package de.eldoria.shepard.commandmodules.reminder.scheduler;

import de.eldoria.shepard.commandmodules.reminder.data.ReminderData;
import de.eldoria.shepard.commandmodules.reminder.types.ReminderComplex;
import de.eldoria.shepard.localization.enums.scheduler.ReminderLocale;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqJDA;
import net.dv8tion.jda.api.JDA;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

public class ReminderScheduler implements Runnable, ReqJDA, ReqInit, ReqDataSource {
    private JDA jda;
    private ReminderData reminderData;

    @Override
    public void run() {
        List<ReminderComplex> expiredReminder = reminderData.getAndDeleteExpiredReminder(jda, null);
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

            MessageSender.sendMessage(localizeAllAndReplace(ReminderLocale.M_REMINDER.tag,
                    reminder.getGuild(), reminder.getUser().getAsMention()) + System.lineSeparator()
                    + "**" + reminder.getText() + "**", reminder.getChannel());
        }
    }

    @Override
    public void addJDA(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void init() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void addDataSource(DataSource source) {
        reminderData = new ReminderData(source);
    }
}
