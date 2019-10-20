package de.eldoria.shepard.database.types;

import de.eldoria.shepard.ShepardBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;

public class ReminderComplex extends ReminderSimple {
    private Guild guild;
    private TextChannel channel;
    private User user;

    public ReminderComplex(String guild_id, String channel_id, String user_id, String text) {
        super(0, text);
        channel = ShepardBot.getJDA().getTextChannelById(channel_id);
        if (channel != null) {
            guild = channel.getGuild();
        } else {
            guild = ShepardBot.getJDA().getGuildById(guild_id);
        }
        user = ShepardBot.getJDA().getUserById(user_id);
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
