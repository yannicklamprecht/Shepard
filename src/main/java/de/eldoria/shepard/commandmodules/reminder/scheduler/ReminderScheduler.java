package de.eldoria.shepard.commandmodules.reminder.scheduler;

import de.eldoria.shepard.basemodules.reactionactions.ReactionActionCollection;
import de.eldoria.shepard.basemodules.reactionactions.actions.SnoozeReminder;
import de.eldoria.shepard.commandmodules.reminder.data.ReminderData;
import de.eldoria.shepard.commandmodules.reminder.types.ReminderComplex;
import de.eldoria.shepard.localization.enums.scheduler.ReminderSchedulerLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqReactionAction;
import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.util.reactions.Emoji;
import de.eldoria.shepard.wrapper.EventWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

@Slf4j
public class ReminderScheduler implements Runnable, ReqShardManager, ReqReactionAction, ReqInit, ReqDataSource {
    private ShardManager shardManager;
    private ReminderData reminderData;
    private ReactionActionCollection reactionAction;

    @Override
    public void run() {
        List<ReminderComplex> expiredReminder = reminderData.getAndDeleteExpiredReminder(shardManager, null);

        for (ReminderComplex reminder : expiredReminder) {
            if (reminder.getUser() == null) {
                return;
            }

            if (reminder.getChannel() == null || reminder.getGuild() == null
                    || reminder.getGuild().getMember(reminder.getUser()) == null) {
                LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder()
                        .setTitle(localizeAllAndReplace(ReminderSchedulerLocale.M_REMINDER.tag,
                                EventWrapper.fakeEmpty(), reminder.getReminderId()))
                        .setDescription(reminder.getText())
                        .setColor(Colors.Pastel.DARK_RED)
                        .setFooter(localizeAllAndReplace(ReminderSchedulerLocale.M_COMMAND.tag,
                                EventWrapper.fakeEmpty(), reminder.getReminderId()));
                reminder.getUser().openPrivateChannel()
                        .flatMap(channel -> channel.sendMessage(builder.build()))
                        .queue();
                return;
            }

            LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(reminder.getGuild())
                    .setTitle(localizeAllAndReplace(ReminderSchedulerLocale.M_REMINDER_GUILD.tag,
                            EventWrapper.fakeEmpty(), reminder.getReminderId(), reminder.getUser().getAsTag()))
                    .setDescription(reminder.getText())
                    .setColor(Colors.Pastel.DARK_RED)
                    .setFooter(localizeAllAndReplace(ReminderSchedulerLocale.M_REACT_COMMAND.tag,
                            reminder.getGuild(), Emoji.ARROWS_COUNTERBLOCKWISE.unicode, reminder.getReminderId()));

            reminder.getChannel().sendMessage(reminder.getUser().getAsMention()).queue();
            Message message = reminder.getChannel().sendMessage(builder.build()).complete();
            reactionAction.addReactionAction(message, new SnoozeReminder(reminderData, reminder, reminder.getUser()));
        }
    }

    @Override
    public void addShardManager(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    public void init() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this, 10, 20, TimeUnit.SECONDS);
    }

    @Override
    public void addDataSource(DataSource source) {
        reminderData = new ReminderData(source);
    }

    @Override
    public void addReactionAction(ReactionActionCollection reactionAction) {
        this.reactionAction = reactionAction;
    }
}
