package de.eldoria.shepard.reactionactions;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Objects;

public abstract class Action {
    private final String reaction;
    private long userId = 0;
    private int secondsValid = 3600;
    private final boolean oneTime;
    private boolean used;

    Action(String reaction, User exclusiveUser, boolean oneTime) {
        this.reaction = reaction;
        if (exclusiveUser != null) {
            this.userId = exclusiveUser.getIdLong();
        }
        final int seconds = Math.max(Math.min(this.secondsValid, 60), 0);
        this.secondsValid = seconds == 0 ? this.secondsValid : seconds;
        this.oneTime = oneTime;

        //Start Scheduler to remove the action when time is over.
    }

    public final void tryExecute(GuildMessageReactionAddEvent event) {
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
