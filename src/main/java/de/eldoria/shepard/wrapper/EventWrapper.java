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
    private long messageId;
    private final User actor;
    private final Message message;
    private Guild guild;
    private boolean fake;

    // Reaction Event data
    private MessageReaction messageReaction;
    private MessageReaction.ReactionEmote reactionEmote;


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
            wrappingException.getThrowable().initCause(e.getCause());
            log.error("Error while wrapping a event.", wrappingException);
            throw wrappingException;
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
            wrappingException.getThrowable().initCause(e.getCause());
            log.error("Error while wrapping a event.", wrappingException);
            throw wrappingException;
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

    public boolean isReactionEvent() {
        return reactionEmote != null && messageReaction != null;
    }

    public MessageReaction.ReactionEmote getReactionEmote() {
        return reactionEmote;
    }

    public MessageReaction getReaction() {
        return messageReaction;
    }

    public EventContext getMessageContext() {
        return context;
    }

    public boolean isGuildEvent() {
        return context == EventContext.GUILD && guild != null;
    }

    public boolean isPrivateEvent() {
        return context == EventContext.PRIVATE;
    }

    public JDA getJDA() {
        return jda;
    }

    public User getAuthor() {
        return actor;
    }

    public User getActor() {
        return actor;
    }

    public MessageChannel getMessageChannel() {
        return messageChannel;
    }

    public Message getMessage() {
        return message;
    }

    public long getMessageIdLong() {
        return message.getIdLong();
    }

    public String getMessageId() {
        return message.getId();
    }

    public Optional<Guild> getGuild() {
        return Optional.ofNullable(guild);
    }

    public Optional<TextChannel> getTextChannel() {
        if (isGuildEvent()) {
            return Optional.ofNullable((TextChannel) messageChannel);
        } else {
            return Optional.empty();
        }
    }

    public Optional<PrivateChannel> getPrivateChannel() {
        if (isPrivateEvent()) {
            return Optional.ofNullable(actor.openPrivateChannel().complete());
        } else {
            return Optional.empty();
        }
    }

    public Optional<Member> getMember() {
        if (guild == null) return Optional.empty();
        return Optional.ofNullable(guild.getMember(actor));
    }

}
