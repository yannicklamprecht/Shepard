package de.eldoria.shepard.localization.enums.fun;

public enum RandomJokeLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.randomJoke.description"),
    /**
     * Localization key for message joke.
     */
    M_JOKE("command.randomJoke.message.joke");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    RandomJokeLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }
}
