package de.eldoria.shepard.commandmodules.reminder.types;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.annotation.Nullable;

public class ReminderComplex extends ReminderSimple {
    private final Guild guild;
    private final TextChannel channel;
    private final User user;

    /**
     * Creates a new reminder object.
     *  @param jda       jda instance
     * @param guildId   guild where the reminder was assigned
     * @param channelId channel where the reminder was assigned
     * @param userId    user which created the reminder
     * @param text      text for reminder
     */
    public ReminderComplex(ShardManager jda, String guildId, String channelId, String userId, String text) {
        super(0, text);
        channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            guild = channel.getGuild();
        } else {
            guild = jda.getGuildById(guildId);
        }
        user = jda.getUserById(userId);
    }

    /**
     * Get the guild, where the reminder was created.
     *
     * @return guild object where the invite was created. can be null
     */
    @Nullable
    public Guild getGuild() {
        return guild;
    }

    /**
     * Get the text channel where the reminder was created.
     *
     * @return text channel. can be null
     */
    @Nullable
    public TextChannel getChannel() {
        return channel;
    }

    /**
     * Get the user who created the event.
     *
     * @return user object. can be null.
     */
    @Nullable
    public User getUser() {
        return user;
    }
}
