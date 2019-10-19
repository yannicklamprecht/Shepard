package de.eldoria.shepard.minigames;

import de.eldoria.shepard.minigames.guessgame.GuessGameEvaluator;
import de.eldoria.shepard.minigames.kudolottery.KudoLotteryEvaluator;

public class EvaluationSchedulerCollection {
    private static EvaluationScheduler<GuessGameEvaluator> guessGameEvaluationSchedulerInstance;
    private static EvaluationScheduler<KudoLotteryEvaluator> kudoLotteryEvaluationSchedulerInstance;


    public static EvaluationScheduler<GuessGameEvaluator> getGuessGameScheduler() {
        if (guessGameEvaluationSchedulerInstance == null) {
            guessGameEvaluationSchedulerInstance = new EvaluationScheduler<>(5);
        }
        return guessGameEvaluationSchedulerInstance;
    }

    public static EvaluationScheduler<KudoLotteryEvaluator> getKudoLotteryScheduler() {
        if (kudoLotteryEvaluationSchedulerInstance == null) {
            kudoLotteryEvaluationSchedulerInstance = new EvaluationScheduler<>(5);
        }
        return kudoLotteryEvaluationSchedulerInstance;
    }
}
