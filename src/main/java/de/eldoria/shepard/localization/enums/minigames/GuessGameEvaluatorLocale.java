package de.eldoria.shepard.localization.enums.minigames;

public enum GuessGameEvaluatorLocale {

    M_TITLE_NSFW("evaluator.guessGame.message.titleNsfw"),
    M_TITLE_SFW("evaluator.guessGame.message.titleSfw"),
    M_MORE_USER("evaluator.guessGame.message.moreUsers"),
    M_EARN("evaluator.guessGame.message.earn"),
    M_CONGRATULATION("evaluator.guessGame.message.congratulation"),
    M_NO_WINNER("evaluator.guessGame.message.noWinner"),
    M_IMAGE_NOT_DISPLAYED("evaluator.guessGame.message.imageNotDisplayed");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    GuessGameEvaluatorLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
