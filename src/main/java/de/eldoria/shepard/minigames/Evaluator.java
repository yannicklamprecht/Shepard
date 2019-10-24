package de.eldoria.shepard.minigames;

import de.eldoria.shepard.minigames.guessgame.GuessGameEvaluator;
import de.eldoria.shepard.minigames.kudolottery.KudoLotteryEvaluator;

public class Evaluator {
    private static ChannelEvaluator<GuessGameEvaluator> guessGameChannelEvaluatorInstance;
    private static ChannelEvaluator<KudoLotteryEvaluator> kudoLotteryChannelEvaluatorInstance;


    public static ChannelEvaluator<GuessGameEvaluator> getGuessGame() {
        if (guessGameChannelEvaluatorInstance == null) {
            guessGameChannelEvaluatorInstance = new ChannelEvaluator<>(5);
        }
        return guessGameChannelEvaluatorInstance;
    }

    public static ChannelEvaluator<KudoLotteryEvaluator> getKudoLotteryScheduler() {
        if (kudoLotteryChannelEvaluatorInstance == null) {
            kudoLotteryChannelEvaluatorInstance = new ChannelEvaluator<>(5);
        }
        return kudoLotteryChannelEvaluatorInstance;
    }
}
