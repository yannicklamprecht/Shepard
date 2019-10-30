package de.eldoria.shepard.localization.enums.minigames;

public enum KudoLotteryEvaluatorLocale {
    M_NO_WINNER("evaluator.kudoLottery.message.noWinner"),
    M_CONGRATULATION("evaluator.kudoLottery.message.congratulation"),
    M_TITLE("evaluator.kudoLottery.message.title"),
    M_DESCRIPTION("evaluator.kudoLottery.message.description"),
    M_FIELD_TITLE("evaluator.kudoLottery.message.fieldTitle"),
    M_FIELD_TEXT("evaluator.kudoLottery.message.fieldText");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    KudoLotteryEvaluatorLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
