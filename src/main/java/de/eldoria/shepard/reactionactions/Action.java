package de.eldoria.shepard.reactionactions;

import de.eldoria.shepard.util.reactions.EmojiCollection;
import de.eldoria.shepard.util.reactions.EmoteCollection;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Objects;

public abstract class Action {
    private final String emoji;
    private final Emote emote;
    private final ReactionType reactionType;
    private final boolean oneTime;
    private long userId;
    private final int secondsValid;
    private boolean used;

    Action(EmojiCollection emojiCollection, User exclusiveUser, int secondsValid, boolean oneTime) {
        this.emoji = emojiCollection.unicode;
        this.emote = null;
        reactionType = ReactionType.EMOJI;
        if (exclusiveUser != null) {
            this.userId = exclusiveUser.getIdLong();
        }
        this.secondsValid = Math.max(60, Math.min(3600, secondsValid));
        this.oneTime = oneTime;
    }

    Action(EmoteCollection emote, User exclusiveUser, int secondsValid, boolean oneTime) {
        this.emote = emote.getEmote();
        this.emoji = null;
        this.reactionType = ReactionType.EMOTE;
        if (exclusiveUser != null) {
            this.userId = exclusiveUser.getIdLong();
        }
        this.secondsValid = Math.max(60, Math.min(3600, secondsValid));
        this.oneTime = oneTime;
    }


    public final void execute(GuildMessageReactionAddEvent event) {
        if (!event.getReactionEmote().isEmoji()) {
            return;
        }

        if (!oneTime || !used) {
            if (event.getReaction().isSelf()) {
                return;
            }
            if (userId != 0 && event.getUser().getIdLong() != userId) {
                return;
            }

            if (event.getReactionEmote().getEmoji().equals(emoji)) {
                internalExecute(event);
                if (oneTime) {
                    used = true;
                }
            }
        }
    }

    protected abstract void internalExecute(GuildMessageReactionAddEvent event);

    public boolean isUsed() {
        return used;
    }

    public String getEmoji() {
        return emoji;
    }

    public Emote getEmote() {
        return emote;
    }

    public ReactionType getReactionType() {
        return reactionType;
    }

    public int getSecondsValid() {
        return secondsValid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return userId == action.userId
                && getEmoji().equals(action.getEmoji());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmoji(), userId);
    }
}
