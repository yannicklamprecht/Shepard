package de.eldoria.shepard.reactionactions;

import de.eldoria.shepard.util.Emoji;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Objects;

public abstract class Action {
    private final String reaction;
    private final boolean oneTime;
    private long userId;
    private int secondsValid;
    private boolean used;

    Action(Emoji reaction, User exclusiveUser, int secondsValid, boolean oneTime) {
        this.reaction = reaction.unicode;
        if (exclusiveUser != null) {
            this.userId = exclusiveUser.getIdLong();
        }
        //TODO seconds should be set by constructor.
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

            if (event.getReactionEmote().getAsCodepoints().equals(reaction)) {
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

    public String getReaction() {
        return reaction;
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
                && getReaction().equals(action.getReaction());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getReaction(), userId);
    }
}
