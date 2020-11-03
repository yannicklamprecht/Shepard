package de.eldoria.shepard.localization.enums.commands.fun;

public enum SportLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.sport.description"),
    REPEAT("command.sport.repeat");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    SportLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}