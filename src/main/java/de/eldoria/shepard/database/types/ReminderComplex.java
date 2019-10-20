package de.eldoria.shepard.database.types;

import de.eldoria.shepard.ShepardBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;

public class ReminderComplex extends ReminderSimple {
    private final Guild guild;
    private final TextChannel channel;
    private final User user;

    public ReminderComplex(String guildId, String channelId, String userId, String text) {
        super(0, text);
        channel = ShepardBot.getJDA().getTextChannelById(channelId);
        if (channel != null) {
            guild = channel.getGuild();
        } else {
            guild = ShepardBot.getJDA().getGuildById(guildId);
        }
        user = ShepardBot.getJDA().getUserById(userId);
    }

    @Nullable
    public Guild getGuild() {
        return guild;
    }

    @Nullable
    public TextChannel getChannel() {
        return channel;
    }

    @Nullable
    public User getUser() {
        return user;
    }
}
