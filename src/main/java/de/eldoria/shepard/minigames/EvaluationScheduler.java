package de.eldoria.shepard.minigames;

import de.eldoria.shepard.collections.UniqueMessageIdentifier;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.w3c.dom.Text;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class EvaluationScheduler<T extends Evaluator> {
    private Map<UniqueMessageIdentifier, T> evaluationChannel = new HashMap<>();
    private ScheduledExecutorService executor;

    public EvaluationScheduler(int poolSize) {
        executor = new ScheduledThreadPoolExecutor(poolSize);

    }

    @Nullable
    public T getChannelEvaluator(TextChannel channel) {
        List<Map.Entry<UniqueMessageIdentifier, T>> collect = evaluationChannel.entrySet()
                .stream().filter(a -> a.getKey().isChannel(channel)).collect(Collectors.toUnmodifiableList());
        if (collect.isEmpty()) {
            return null;
        }
        return collect.get(0).getValue();
    }

    /**
     * Schedules a evaluation.
     * @param message
     * @param evaluator
     */
    public void scheduleEvaluation(Message message, int seconds, T evaluator) {
        executor.schedule(evaluator, seconds, TimeUnit.SECONDS);
        evaluationChannel.put(new UniqueMessageIdentifier(message.getTextChannel(), message.getIdLong()), evaluator);
    }


    /**
     * Check if a message is used for voting.
     * @param uniqueMessageIdentifier identifier for message.
     * @return true if it is a voting message.
     */
    public boolean isReactionMessage(UniqueMessageIdentifier uniqueMessageIdentifier) {
        return evaluationChannel.get(uniqueMessageIdentifier) != null;
    }

    /**
     * Check if a channel is in a evaluation process.
     * @param channel channel for lookup
     * @return true if a evaluation is in progress in this channel
     */
    public boolean isEvaluationActive(TextChannel channel){
        return getChannelEvaluator(channel) != null;
    }

    /**
     * Marks a evaluation for a channel as done.
     * @param channel channel to remove.
     */
    public void evaluationDone(TextChannel channel) {
        List<Map.Entry<UniqueMessageIdentifier, T>> collect = evaluationChannel.entrySet()
                .stream().filter(a -> a.getKey().isChannel(channel)).collect(Collectors.toUnmodifiableList());
        if (!collect.isEmpty()) {
            evaluationChannel.remove(collect.get(0).getKey());
        }
    }


}
