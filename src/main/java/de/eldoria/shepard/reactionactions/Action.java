package de.eldoria.shepard.reactionactions;

import de.eldoria.shepard.util.reactions.Emoji;
import de.eldoria.shepard.util.reactions.ShepardEmote;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Objects;

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
        this.secondsValid = Math.max(60, Math.min(3600, secondsValid));
        this.oneTime = oneTime;
    }

    /**
     * Creates a new action with a emoji.
     *
     * @param emote         emote from emote collection
     * @param exclusiveUser user which is allowed to user the action. null if action is not user exclusive
     * @param secondsValid  how long the action can be executed
     * @param oneTime       true if the action can be used only one time
     */
    Action(ShepardEmote emote, User exclusiveUser, int secondsValid, boolean oneTime) {
        this.emote = emote.getEmote();
        this.emoji = null;
        this.reactionType = ReactionType.EMOTE;
        if (exclusiveUser != null) {
            this.userId = exclusiveUser.getIdLong();
        }
        this.secondsValid = Math.max(60, Math.min(3600, secondsValid));
        this.oneTime = oneTime;
    }

    /**
     * Execute the action if the right emoji or emote is pressed.
     *
     * @param event event for action information.
     */
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

    /**
     * Execute the internal functions.
     *
     * @param event event for information
     */
    protected abstract void internalExecute(GuildMessageReactionAddEvent event);

    /**
     * Check if the action is used and cant be used anymore.
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
