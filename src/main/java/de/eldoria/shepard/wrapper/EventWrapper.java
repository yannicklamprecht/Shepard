package de.eldoria.shepard.wrapper;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.Optional;

@Slf4j
public class EventWrapper {
    private static final EventWrapper empty = new EventWrapper(null, null, null, null).asFaked();

    // General Event data
    private final JDA jda;
    private EventContext context;
    private final MessageChannel messageChannel;
    private long messageIdLong;
    private String messageId;
    private final User actor;
    private final Message message;
    private Guild guild;
    private boolean fake;

    // Reaction Event data
    private MessageReaction messageReaction;
    private MessageReaction.ReactionEmote reactionEmote;

    public EventWrapper(JDA jda, EventContext context, MessageChannel messageChannel, long messageIdLong,
                        String messageId, User actor, Message message, Guild guild, MessageReaction messageReaction,
                        MessageReaction.ReactionEmote reactionEmote) {
        this.jda = jda;
        this.context = context;
        this.messageChannel = messageChannel;
        this.messageIdLong = messageIdLong;
        this.messageId = messageId;
        this.actor = actor;
        this.message = message;
        this.guild = guild;
        this.messageReaction = messageReaction;
        this.reactionEmote = reactionEmote;
    }

    private EventWrapper(JDA jda, MessageChannel channel, User user, Message message, Guild guild) {
        this(jda, channel, user, message);
        this.guild = guild;
        context = EventContext.GUILD;
    }

    private EventWrapper(JDA jda, MessageChannel channel, User user, Message message) {
        this.jda = jda;
        this.messageChannel = channel;
        this.actor = user;
        this.message = message;
        this.messageId = message == null ? null : message.getId();
        this.messageIdLong = message == null ? Long.MIN_VALUE : message.getIdLong();
        context = EventContext.PRIVATE;
    }

    private EventWrapper(JDA jda, MessageChannel channel, User user, Message message, Guild guild, MessageReaction messageReaction) {
        this(jda, channel, user, message, guild);
        this.messageReaction = messageReaction;
        this.reactionEmote = messageReaction.getReactionEmote();
    }

    private EventWrapper(JDA jda, MessageChannel channel, User user, Message message, MessageReaction messageReaction) {
        this(jda, channel, user, message);
        this.messageReaction = messageReaction;
        this.reactionEmote = messageReaction.getReactionEmote();
    }

    public static EventWrapper wrap(PrivateMessageReceivedEvent event) {
        return new EventWrapper(event.getJDA(), event.getChannel(), event.getAuthor(), event.getMessage());
    }

    public static EventWrapper wrap(PrivateMessageUpdateEvent event) {
        return new EventWrapper(event.getJDA(), event.getChannel(), event.getAuthor(), event.getMessage());
    }

    public static EventWrapper wrap(GuildMessageUpdateEvent event) {
        return new EventWrapper(event.getJDA(), event.getChannel(), event.getAuthor(), event.getMessage(),
                event.getGuild());
    }

    public static EventWrapper wrap(GuildMessageReceivedEvent event) {
        return new EventWrapper(event.getJDA(), event.getChannel(), event.getAuthor(), event.getMessage(),
                event.getGuild());
    }

    public static EventWrapper wrap(GuildMessageReactionAddEvent event) throws WrappingException {
        Message messageById;
        try {
            messageById = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        } catch (RuntimeException e) {
            WrappingException wrappingException = new WrappingException(event, e.getCause());
            log.error("Error while wrapping a event.", wrappingException);
            log.error("Caused by", e);
            return new EventWrapper(event.getJDA(), EventContext.GUILD, event.getChannel(), event.getMessageIdLong(),
                    event.getMessageId(), event.getUser(), null, event.getGuild(), event.getReaction(), event.getReactionEmote());
        }

        return new EventWrapper(event.getJDA(), event.getChannel(), event.getUser(), messageById,
                event.getGuild(), event.getReaction());
    }

    public static EventWrapper wrap(PrivateMessageReactionAddEvent event) throws WrappingException {
        Message messageById;
        try {
            messageById = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        } catch (RuntimeException e) {
            WrappingException wrappingException = new WrappingException(event, e.getCause());
            log.error("Error while wrapping a event.", wrappingException);
            log.error("Caused by", e);
            return new EventWrapper(event.getJDA(), EventContext.GUILD, event.getChannel(), event.getMessageIdLong(),
                    event.getMessageId(), event.getUser(), null, null, event.getReaction(),
                    event.getReactionEmote());
        }
        return new EventWrapper(event.getJDA(), event.getChannel(), event.getUser(), messageById, event.getReaction());
    }

    public static EventWrapper fakePrivateMessage(ShardManager shardManager, User user, Message message) {
        PrivateChannel channel = user.openPrivateChannel().complete();
        return new EventWrapper(shardManager.getShardById(0), channel, user, message).asFaked();
    }

    public static EventWrapper fakeGuildEvent(User user, MessageChannel channel, Message message, Guild guild) {
        return new EventWrapper(guild == null ? null : guild.getJDA(), channel, user, message, guild).asFaked();
    }


    /**
     * Get a empty message wrapper.
     * Use this if you need a message wrapper which does not interact with a user or any message.
     * All values are set to null.
     *
     * @return empty message wrapper
     */
    public static EventWrapper fakeEmpty() {
        return empty;
    }

    private EventWrapper asFaked() {
        fake = true;
        return this;
    }

    /**
     * Get if the event was a reaction event.
     *
     * @return true if the event was fired in a private channel
     */
    public boolean isReactionEvent() {
        return reactionEmote != null && messageReaction != null;
    }

    /**
     * Get the message reaction emote.
     *
     * @return this is present if {@link #isReactionEvent()} is true.
     */
    public Optional<MessageReaction.ReactionEmote> getReactionEmote() {
        return Optional.ofNullable(reactionEmote);
    }

    /**
     * Get the message Reaction.
     *
     * @return this is present if {@link #isReactionEvent()} is true.
     */
    public Optional<MessageReaction> getReaction() {
        return Optional.ofNullable(messageReaction);
    }

    /**
     * Get if the event was in a private channel.
     *
     * @return true if the event was fired in a private channel
     */
    public boolean isGuildEvent() {
        return context == EventContext.GUILD && guild != null;
    }

    /**
     * Get if the event was in a private channel.
     *
     * @return true if the event was fired in a private channel
     */
    public boolean isPrivateEvent() {
        return context == EventContext.PRIVATE;
    }

    /**
     * Get the jda which received this event.
     *
     * @return jda instance
     */
    public JDA getJDA() {
        return jda;
    }

    /**
     * Get the user of the event. Equal to {@link #getActor()} ()}
     *
     * @return user of the event
     */
    public User getAuthor() {
        return actor;
    }

    /**
     * Get the actor of the event. Equal to {@link #getAuthor()}
     *
     * @return actor of the event
     */
    public User getActor() {
        return actor;
    }

    /**
     * Get the message channel of the event.
     *
     * @return message channel where the event was fired.
     */
    public MessageChannel getMessageChannel() {
        return messageChannel;
    }

    /**
     * Returns the message.
     *
     * @return returns a message. This is only present when the message could be retreived.
     * This can be empty if the event is a reaction event on a large guild.
     */
    public Optional<Message> getMessage() {
        return Optional.ofNullable(message);
    }

    /**
     * Returns the message id.
     *
     * @return message id as long.
     */
    public long getMessageIdLong() {
        return messageIdLong;
    }

    /**
     * Returns the message id.
     *
     * @return message id as string.
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Returns a optional guild.
     *
     * @return optional with a guild which is always and only present if {@link #isGuildEvent()} is true.
     */
    public Optional<Guild> getGuild() {
        return Optional.ofNullable(guild);
    }

    /**
     * Returns a optional text channel.
     *
     * @return optional with a text channel which is always and only present if {@link #isGuildEvent()} is true.
     */
    public Optional<TextChannel> getTextChannel() {
        if (isGuildEvent()) {
            return Optional.ofNullable((TextChannel) messageChannel);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns a optional private channel.
     *
     * @return optional with a private channel which is always and only present if {@link #isPrivateEvent()} is true.
     */
    public Optional<PrivateChannel> getPrivateChannel() {
        if (isPrivateEvent()) {
            return Optional.ofNullable(actor.openPrivateChannel().complete());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns a optional guild member.
     *
     * @return optional with a private channel which is always and only present if {@link #isGuildEvent()} is true.
     */
    public Optional<Member> getMember() {
        if (guild == null) return Optional.empty();
        return Optional.ofNullable(guild.getMember(actor));
    }

}
