package de.eldoria.shepard.localization.enums.commands.fun;

public enum TailpatLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.tailpat.description"),
    /**
     * Localization key for positive answers.
     */
    ANSWER_POSITIVE("command.tailpat.answer.positive"),
    /**
     * Localization key for negative answers.
     */
    ANSWER_NEGATIVE("command.tailpat.answer.negative"),
    C_SOMEONE("command.tailpat.command.someone"),
    /**
     * Localization key for message answer.
     */
    M_ANSWER_NEGATIVE("command.tailpat.message.answerNegative"),
    M_ANSWER_POSITIVE("command.tailpat.message.answerPositive");


    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    TailpatLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
