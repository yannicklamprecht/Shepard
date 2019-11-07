package de.eldoria.shepard.localization.enums.minigames;

public enum KudoLotteryEvaluatorLocale {
    /**
     * Localization key for message .
     */
    M_NO_WINNER("evaluator.kudoLottery.message.noWinner"),
    /**
     * Localization key for message congratulation.
     */
    M_CONGRATULATION("evaluator.kudoLottery.message.congratulation");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     * @param localeCode locale code
     */
    KudoLotteryEvaluatorLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
