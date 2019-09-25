package de.eldoria.shepard.collections;

import de.eldoria.shepard.reactionactions.Action;
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

public final class ReactionActionCollection {
    private static ReactionActionCollection instance;
    private final Map<UniqueMessageIdentifier, List<Action>> reactionActions = new HashMap<>();
    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(10);

    public static ReactionActionCollection getInstance() {
        if (instance == null) {
            instance = new ReactionActionCollection();
        }
        return instance;
    }

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

    public void addReactionAction(TextChannel channel, Message message, Action action) {
        UniqueMessageIdentifier umi = new UniqueMessageIdentifier(channel, message.getIdLong());
        if (!reactionActions.containsKey(umi)) {
            reactionActions.put(umi, new ArrayList<>());
        }
        reactionActions.get(umi).add(action);

        executorService.schedule(new ActionRemover(umi, action), action.getSecondsValid(), TimeUnit.SECONDS);
    }

    void removeAction(UniqueMessageIdentifier umi, Action action) {
        if (reactionActions.containsKey(umi)) {
            reactionActions.get(umi).removeIf(action::equals);
        }
    }
}
