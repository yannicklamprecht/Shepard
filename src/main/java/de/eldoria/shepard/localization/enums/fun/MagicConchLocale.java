package de.eldoria.shepard.localization.enums.fun;

public enum MagicConchLocale {
    DESCRIPTION("command.magicConch.description"),
    ANSWER_POSITIVE("command.magicConch.answer.positive"),
    ANSWER_NEUTRAL("command.magicConch.answer.neutral"),
    ANSWER_NEGATIVE("command.magicConch.answer.negative"),
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
