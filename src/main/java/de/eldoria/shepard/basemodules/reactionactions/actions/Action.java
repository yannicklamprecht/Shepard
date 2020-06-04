package de.eldoria.shepard.basemodules.reactionactions.actions;

import de.eldoria.shepard.basemodules.reactionactions.util.ReactionType;
import de.eldoria.shepard.util.reactions.Emoji;
import de.eldoria.shepard.wrapper.EventWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.User;

import java.util.Objects;

@Slf4j
public abstract class Action {
    private final String emoji;
    private final Emote emote;
    private final ReactionType reactionType;
    private final boolean oneTime;
    private final int secondsValid;
    private long userId;
    private boolean used;

    /**
     * Creates a new action with a emoji.
     *
     * @param emoji         emoji from emoji collection
     * @param exclusiveUser user which is allowed to user the action. null if action is not user exclusive
     * @param secondsValid  how long the action can be executed
     * @param oneTime       true if the action can be used only one time
     */
    Action(Emoji emoji, User exclusiveUser, int secondsValid, boolean oneTime) {
        this.emoji = emoji.unicode;
        this.emote = null;
        reactionType = ReactionType.EMOJI;
        if (exclusiveUser != null) {
            this.userId = exclusiveUser.getIdLong();
        }
        this.secondsValid = Math.max(60, Math.min(60 * 60, secondsValid));
        this.oneTime = oneTime;
    }

    /**
     * Execute the action if the right emoji or emote is pressed.
     *
     * @param event event for action information.
     */
    public final void execute(EventWrapper event) {
        if (!event.isReactionEvent()) {
            log.error("A non reaction event wrapper was passed to a reaction action");
            return;
        }
        if (!event.getReactionEmote().get().isEmoji()) {
            return;
        }

        if (!oneTime || !used) {
            if (event.getReaction().get().isSelf()) {
                return;
            }
            if (userId != 0 && event.getActor().getIdLong() != userId) {
                return;
            }

            if (event.getReactionEmote().get().getEmoji().equals(emoji)) {
                internalExecute(event);
                if (oneTime) {
                    used = true;
                }
            }
        }
    }

    /**
     * Execute the internal functions.
     *
     * @param wrapper wrapper for information
     */
    protected abstract void internalExecute(EventWrapper wrapper);

    /**
     * Check if the action is used and cant be used anymore.
     * This only applies to {@link #oneTime} actions.
     *
     * @return true if action is used
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * Get the emoji.
     *
     * @return emoji as string (unicode) or null
     */
    public String getEmoji() {
        return emoji;
    }

    /**
     * Get the emote.
     *
     * @return emote object or null
     */
    public Emote getEmote() {
        return emote;
    }

    /**
     * Get the reaction type.
     *
     * @return reaction type
     */
    public ReactionType getReactionType() {
        return reactionType;
    }

    /**
     * Get the time a action can be executed.
     *
     * @return time in seconds as integer.
     */
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
