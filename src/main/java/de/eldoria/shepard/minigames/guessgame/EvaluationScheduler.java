package de.eldoria.shepard.minigames.guessgame;

import de.eldoria.shepard.database.types.HentaiImage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EvaluationScheduler {
    private static EvaluationScheduler instance;
    private Map<Long, Evaluator> evaluationChannel = new HashMap<>();

    private ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(5);

    public static void scheduleEvaluation(Message message, HentaiImage image) {
        Evaluator evaluator = new Evaluator(message, image);
        getInstance().executor.schedule(evaluator, 30, TimeUnit.SECONDS);
        getInstance().evaluationChannel.put(message.getChannel().getIdLong(), evaluator);
    }

    private static EvaluationScheduler getInstance() {
        if (instance == null) {
            instance = new EvaluationScheduler();
        }
        return instance;
    }

    public static boolean evaluationInProgress(TextChannel channel) {
        return getInstance().evaluationChannel.containsKey(channel.getIdLong());
    }

    static void evaluationDone(long channelId) {
        getInstance().evaluationChannel.remove(channelId);
    }

    @Nullable
    public static Evaluator getChannelEvaluator(TextChannel channel) {
        return getInstance().evaluationChannel.get(channel.getIdLong());
    }
}
