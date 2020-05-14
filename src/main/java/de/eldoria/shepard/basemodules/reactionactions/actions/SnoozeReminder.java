package de.eldoria.shepard.basemodules.reactionactions.actions;

import de.eldoria.shepard.commandmodules.reminder.data.ReminderData;
import de.eldoria.shepard.commandmodules.reminder.types.ReminderComplex;
import de.eldoria.shepard.localization.enums.scheduler.ReminderSchedulerLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.util.reactions.Emoji;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.User;

import static de.eldoria.shepard.localization.enums.commands.util.ReminderLocal.M_TITLE_SNOOZED;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

public class SnoozeReminder extends Action {

    private ReminderData reminderData;
    private final ReminderComplex reminder;

    public SnoozeReminder(ReminderData reminderData, ReminderComplex reminder, User user) {
        super(Emoji.ARROWS_COUNTERBLOCKWISE, user, Integer.MAX_VALUE, true);
        this.reminderData = reminderData;
        this.reminder = reminder;
    }

    @Override
    protected void internalExecute(EventWrapper wrapper) {
        int minutes = Math.min((reminder.getSnoozeCount() + 1) * 30, 12 * 60);

        reminderData.snoozeReminder(reminder.getReminderIdLong(), minutes + " min",
                EventWrapper.fakeGuildEvent(reminder.getUser(), reminder.getChannel(), null, reminder.getGuild()));

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(wrapper)
                .setTitle(localizeAllAndReplace(M_TITLE_SNOOZED.tag, wrapper, reminder.getReminderId()))
                .setDescription(localizeAllAndReplace(ReminderSchedulerLocale.M_REMINDER_SNOOZED.tag,
                        wrapper, (minutes / 60.0) + ""));
        wrapper.getMessageChannel().sendMessage(builder.build()).queue();
    }
}
