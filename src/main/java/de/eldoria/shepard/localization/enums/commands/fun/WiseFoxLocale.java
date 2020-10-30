package de.eldoria.shepard.localization.enums.commands.fun;

public enum WiseFoxLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.wiseFox.description"),
    /**
     * Localization key for positive answers.
     */
    ANSWER_POSITIVE("command.wiseFox.answer.positive"),
    /**
     * Localization key for neutral answers.
     */
    ANSWER_NEUTRAL("command.wiseFox.answer.neutral"),
    /**
     * Localization key for negative answers.
     */
    ANSWER_NEGATIVE("command.wiseFox.answer.negative"),
    /**
     * Localization key for message answer.
     */
    M_ANSWER("command.wiseFox.message.answer");


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
    WiseFoxLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
