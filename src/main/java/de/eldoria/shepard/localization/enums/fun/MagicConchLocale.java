package de.eldoria.shepard.localization.enums.fun;

public enum MagicConchLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.magicConch.description"),
    /**
     * Localization key for positive answers.
     */
    ANSWER_POSITIVE("command.magicConch.answer.positive"),
    /**
     * Localization key for neutral answers.
     */
    ANSWER_NEUTRAL("command.magicConch.answer.neutral"),
    /**
     * Localization key for negative answers.
     */
    ANSWER_NEGATIVE("command.magicConch.answer.negative"),
    /**
     * Localization key for message answer.
     */
    M_ANSWER("command.magicConch.message.answer");


    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    MagicConchLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }

}
