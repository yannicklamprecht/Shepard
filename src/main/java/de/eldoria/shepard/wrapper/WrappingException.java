package de.eldoria.shepard.wrapper;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;

public class WrappingException extends RuntimeException {

    private final Throwable throwable;

    public WrappingException(Throwable throwable) {
        this("Could not wrap event.", throwable);
    }

    public WrappingException(String message, Throwable throwable) {
        super(message);
        this.throwable = throwable;
    }

    public WrappingException(GuildMessageReactionAddEvent event, Throwable throwable) {
        this(String.format("Could not wrap guild message reaction add event.\n"
                        + "The event was fired on guild %s(%s) in Channel %s(%s).\n"
                        + "The user was %s (%s) with %s %s", event.getGuild().getName(), event.getGuild().getId(),
                event.getChannel().getName(), event.getChannel().getId(),
                event.getUser().getAsTag(),
                event.getUser().getId(),
                event.getReaction().getReactionEmote().isEmoji()
                        ? "emoji" : "custom emote",
                event.getReaction().getReactionEmote().isEmoji()
                        ? event.getReaction().getReactionEmote().getEmoji()
                        : event.getReaction().getReactionEmote().getEmote().getAsMention()), throwable);
    }

    public WrappingException(PrivateMessageReactionAddEvent event, Throwable throwable) {
        this(String.format("Could not wrap guild message reaction add event.\n"
                        + "The event was fired on private message\n"
                        + "The user was %s (%s) with %s %s",
                event.getUser().getAsTag(),
                event.getUser().getId(),
                event.getReaction().getReactionEmote().isEmoji()
                        ? "emoji" : "custom emote",
                event.getReaction().getReactionEmote().isEmoji()
                        ? event.getReaction().getReactionEmote().getEmoji()
                        : event.getReaction().getReactionEmote().getEmote().getAsMention()), throwable);
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
