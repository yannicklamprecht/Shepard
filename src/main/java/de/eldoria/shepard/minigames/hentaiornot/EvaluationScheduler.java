package de.eldoria.shepard.minigames.hentaiornot;

import de.eldoria.shepard.database.types.HentaiImage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EvaluationScheduler {
    private static EvaluationScheduler instance;
    private Set<Long> evaluationChannel = new HashSet<>();

    private ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(5);

    public static void scheduleEvaluation(Message message, HentaiImage image) {
        getInstance().executor.schedule(new Evaluator(message, image), 30, TimeUnit.SECONDS);
    }

    private static EvaluationScheduler getInstance() {
        if (instance == null) {
            instance = new EvaluationScheduler();
        }
        return instance;
    }

    public static boolean evaluationInProgress(TextChannel channel) {
        return getInstance().evaluationChannel.contains(channel.getIdLong());
    }

    static void evaluationDone(long channelId) {
        getInstance().evaluationChannel.removeIf(id -> id == channelId);
    }


}
