package de.eldoria.shepard.commandmodules.reminder.types;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.time.Instant;

@Getter
public class ReminderComplex extends ReminderSimple {
    private final Guild guild;
    private final TextChannel channel;
    private final User user;
    private final Instant time;
    private final int snoozeCount;

    /**
     * Creates a new reminder object.
     *
     * @param jda          jda instance
     * @param id           id of the reminder
     * @param guildId      guild where the reminder was assigned
     * @param channelId    channel where the reminder was assigned
     * @param userId       user which created the reminder
     * @param text         text for reminder
     * @param snoozeCount times the reminder was snoozed
     */
    public ReminderComplex(ShardManager jda, long id, long guildId, long channelId, long userId, String text, int snoozeCount) {
        this(jda, id, guildId, channelId, userId, text, Instant.now(), snoozeCount);
    }

    /**
     * Creates a new reminder object.
     *
     * @param jda          jda instance
     * @param guildId      guild where the reminder was assigned
     * @param channelId    channel where the reminder was assigned
     * @param userId       user which created the reminder
     * @param text         text for reminder
     * @param snoozeCount
     */
    public ReminderComplex(ShardManager jda, long id, long guildId, long channelId, long userId, String text, Instant time, int snoozeCount) {
        super(id, text);
        this.time = time;
        this.snoozeCount = snoozeCount;
        channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            guild = channel.getGuild();
        } else {
            guild = jda.getGuildById(guildId);
        }
        user = jda.getUserById(userId);
    }
}
