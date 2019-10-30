package de.eldoria.shepard.util;

import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Objects;

public class UniqueMessageIdentifier {
    private final long guildId;
    private final long channelId;
    private final long messageId;

    public UniqueMessageIdentifier(TextChannel channel, long message) {
        guildId = channel.getGuild().getIdLong();
        channelId = channel.getIdLong();
        messageId = message;
    }

    public boolean isChannel(TextChannel channel) {
        return channel.getIdLong() == channelId && channel.getGuild().getIdLong() == guildId;
    }

    private long getGuildId() {
        return guildId;
    }

    private long getChannelId() {
        return channelId;
    }

    private long getMessageId() {
        return messageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UniqueMessageIdentifier that = (UniqueMessageIdentifier) o;
        return getGuildId() == that.getGuildId()
                && getChannelId() == that.getChannelId()
                && getMessageId() == that.getMessageId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGuildId(), getChannelId(), getMessageId());
    }
}
