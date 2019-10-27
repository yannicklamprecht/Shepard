package de.eldoria.shepard.localization.enums.fun;

public enum OwoLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.owo.description");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    OwoLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }
}