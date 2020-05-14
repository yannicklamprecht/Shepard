package de.eldoria.shepard.util;

import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Objects;

public final class UniqueMessageIdentifier {
    private final long guildId;
    private final long channelId;
    private final long messageId;

    /**
     * Creates a new unique message identifier.
     *
     * @param channel channel where the message is in.
     * @param message message id
     */
    private UniqueMessageIdentifier(TextChannel channel, long message) {
        guildId = channel.getGuild().getIdLong();
        channelId = channel.getIdLong();
        messageId = message;
    }

    private UniqueMessageIdentifier(MessageChannel channel, long message) {
        guildId = 0L;
        channelId = channel.getIdLong();
        messageId = message;
    }

    /**
     * Get a unique message identifier.
     *
     * @param wrapper event data
     * @return a new unique message identifier for the message
     */
    public static UniqueMessageIdentifier get(EventWrapper wrapper) {
        if (wrapper.isGuildEvent()) {
            return new UniqueMessageIdentifier(wrapper.getTextChannel().get(), wrapper.getMessageIdLong());
        } else {
            return new UniqueMessageIdentifier(wrapper.getMessageChannel(), wrapper.getMessageIdLong());
        }
    }

    /**
     * Creates a new unique message identifier.
     *
     * @param messageContext event data
     * @return a new unique message identifier for the message
     */
    public static UniqueMessageIdentifier get(GuildMessageReactionAddEvent messageContext) {
        return new UniqueMessageIdentifier(messageContext.getChannel(), messageContext.getMessageIdLong());
    }

    /**
     * Creates a new unique message identifier.
     *
     * @param message message to create the identifier
     * @return a new unique message identifier for the message
     */
    public static UniqueMessageIdentifier get(Message message) {
        return new UniqueMessageIdentifier(message.getChannel(), message.getIdLong());
    }

    /**
     * check if identifier channel is the same.
     *
     * @param channel channel for check
     * @return true if the snowflake is the same
     */
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
