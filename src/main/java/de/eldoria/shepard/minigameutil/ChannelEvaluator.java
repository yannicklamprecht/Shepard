package de.eldoria.shepard.minigameutil;

import de.eldoria.shepard.util.UniqueMessageIdentifier;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ChannelEvaluator<T extends BaseEvaluator> {

    private final Map<UniqueMessageIdentifier, T> evaluationChannel;
    private final ScheduledExecutorService executor;

    /**
     * Creates a new channel evaluator.
     *
     * @param poolSize pool size of evaluator.
     */
    public ChannelEvaluator(int poolSize) {
        this.evaluationChannel = new HashMap<>();
        executor = new ScheduledThreadPoolExecutor(poolSize);

    }

    /**
     * Get the channel evaluator for the channel.
     *
     * @param channel channel for lookup
     *
     * @return evaluator of null if no evaluation is in progress
     */
    @Nullable
    public T getChannelEvaluator(TextChannel channel) {
        Optional<Map.Entry<UniqueMessageIdentifier, T>> collect = evaluationChannel.entrySet()
                .stream().filter(a -> a.getKey().isChannel(channel)).findFirst();
        return collect.map(Map.Entry::getValue).orElse(null);
    }

    /**
     * Schedules a evaluation.
     *
     * @param seconds   seconds till evaluation.
     * @param evaluator evaluator for evaluation
     */
    public void scheduleEvaluation(int seconds, T evaluator) {
        Optional<Message> start = evaluator.start();
        if (start.isEmpty()) return;
        evaluator.messageId = start.get().getIdLong();
        executor.schedule(evaluator, seconds, TimeUnit.SECONDS);
        evaluationChannel.put(UniqueMessageIdentifier.get(start.get()), evaluator);
    }


    /**
     * Check if a message is used for voting.
     *
     * @param uniqueMessageIdentifier identifier for message.
     *
     * @return true if it is a voting message.
     */
    public boolean isReactionMessage(UniqueMessageIdentifier uniqueMessageIdentifier) {
        return evaluationChannel.get(uniqueMessageIdentifier) != null;
    }

    /**
     * Check if a channel is in a evaluation process.
     *
     * @param channel channel for lookup
     *
     * @return true if a evaluation is in progress in this channel
     */
    public boolean isEvaluationActive(TextChannel channel) {
        return getChannelEvaluator(channel) != null;
    }

    /**
     * Marks a evaluation for a channel as done.
     *
     * @param channel channel to remove.
     */
    public void evaluationDone(TextChannel channel) {
        Optional<Map.Entry<UniqueMessageIdentifier, T>> collect = evaluationChannel.entrySet()
                .stream().filter(a -> a.getKey().isChannel(channel)).findFirst();

        collect.ifPresent(uniqueMessageIdentifierTEntry ->
                evaluationChannel.remove(uniqueMessageIdentifierTEntry.getKey()));
    }
}
