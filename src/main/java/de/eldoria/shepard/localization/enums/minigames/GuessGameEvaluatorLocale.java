package de.eldoria.shepard.localization.enums.minigames;

public enum GuessGameEvaluatorLocale {

    /**
     * Localization key for message nsfw title.
     */
    M_TITLE_NSFW("evaluator.guessGame.message.titleNsfw"),
    /**
     * Localization key for message sfw title.
     */
    M_TITLE_SFW("evaluator.guessGame.message.titleSfw"),
    /**
     * Localization key for message more user.
     */
    M_MORE_USER("evaluator.guessGame.message.moreUsers"),
    /**
     * Localization key for message earn.
     */
    M_EARN("evaluator.guessGame.message.earn"),
    /**
     * Localization key for message congratulation.
     */
    M_CONGRATULATION("evaluator.guessGame.message.congratulation"),
    /**
     * Localization key for message no winner.
     */
    M_NO_WINNER("evaluator.guessGame.message.noWinner"),
    /**
     * Localization key for message image not displayed.
     */
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
