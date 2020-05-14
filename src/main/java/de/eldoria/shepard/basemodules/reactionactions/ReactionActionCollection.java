package de.eldoria.shepard.basemodules.reactionactions;

import de.eldoria.shepard.basemodules.reactionactions.actions.Action;
import de.eldoria.shepard.basemodules.reactionactions.util.ActionRemover;
import de.eldoria.shepard.basemodules.reactionactions.util.ReactionType;
import de.eldoria.shepard.util.UniqueMessageIdentifier;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Collection to cache all emotes on which shepard should react.
 * Hold a list of {@link Action} objects.
 * Registered Actions via {@link #addReactionAction(Message, Action...)} must be removed by
 * {@link #removeAction(UniqueMessageIdentifier, Action)} if the reaction is not {@link Action#isUsed()}.
 */
public class ReactionActionCollection {
    private final Map<UniqueMessageIdentifier, List<Action>> reactionActions = new HashMap<>();
    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(10);

    /**
     * Create a new reaction action collection.
     */
    public ReactionActionCollection() {

    }

    /**
     * Invokes a reaction.
     * Checks if a action is registered for this action in the message and if the reaction is registered.
     * If true executes the action.
     *
     * @param event event for check
     */
    public void invokeReactionAction(EventWrapper event) {
        UniqueMessageIdentifier umi = UniqueMessageIdentifier.get(event);
        if (reactionActions.containsKey(umi)) {
            List<Action> actions = reactionActions.get(umi);
            actions.removeIf(Action::isUsed);
            if (actions.isEmpty()) {
                reactionActions.remove(umi);
            } else {
                actions.forEach(action -> action.execute(event));
            }
        }
    }

    /**
     * Adds a action on a message. Also adds the reaction of the action on the message.
     *
     * @param message message to add
     * @param actions actions to register
     */
    public void addReactionAction(Message message, Action... actions) {
        for (var action : actions) {
            UniqueMessageIdentifier umi = UniqueMessageIdentifier.get(message);
            reactionActions.putIfAbsent(umi, new ArrayList<>());
            reactionActions.get(umi).add(action);

            executorService.schedule(new ActionRemover(umi, action), action.getSecondsValid(), TimeUnit.SECONDS);

            //
            if (action.getReactionType() == ReactionType.EMOJI) {
                message.addReaction(action.getEmoji()).queue();
            } else {
                message.addReaction(action.getEmote()).queue();
            }
        }
    }

    /**
     * Removes a action from a message.
     *
     * @param umi    umi for message identification
     * @param action action to remove
     */
    public void removeAction(UniqueMessageIdentifier umi, Action action) {
        if (reactionActions.containsKey(umi)) {
            reactionActions.get(umi).removeIf(action::equals);
        }
    }
}
