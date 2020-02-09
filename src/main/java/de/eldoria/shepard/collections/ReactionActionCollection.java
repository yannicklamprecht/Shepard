package de.eldoria.shepard.collections;

import de.eldoria.shepard.reactionactions.Action;
import de.eldoria.shepard.util.ActionRemover;
import de.eldoria.shepard.util.UniqueMessageIdentifier;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
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
 * Registered Actions via {@link #addReactionAction(TextChannel, Message, Action)} must be removed by
 * {@link #removeAction(UniqueMessageIdentifier, Action)} if the reaction is not {@link Action#isUsed()}.
 *
 */
public final class ReactionActionCollection {
    private static ReactionActionCollection instance;
    private final Map<UniqueMessageIdentifier, List<Action>> reactionActions = new HashMap<>();
    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(10);

    /**
     * Get the reaction action collection instance.
     *
     * @return instance singleton
     */
    public static ReactionActionCollection getInstance() {
        if (instance == null) {
            instance = new ReactionActionCollection();
        }
        return instance;
    }

    /**
     * Invokes a reaction.
     * Checks if a action is registered for this action in the message and if the reaction is registered.
     * If true executes the action.
     *
     * @param event event for check
     */
    public void invokeReactionAction(GuildMessageReactionAddEvent event) {
        UniqueMessageIdentifier umi = new UniqueMessageIdentifier(event.getChannel(), event.getMessageIdLong());
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
     * @param channel channel where the message is
     * @param message message to add
     * @param action  action to register
     */
    public void addReactionAction(TextChannel channel, Message message, Action action) {
        UniqueMessageIdentifier umi = new UniqueMessageIdentifier(channel, message.getIdLong());
        reactionActions.putIfAbsent(umi, new ArrayList<>());
        reactionActions.get(umi).add(action);

        executorService.schedule(new ActionRemover(umi, action), action.getSecondsValid(), TimeUnit.SECONDS);
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
