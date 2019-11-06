package de.eldoria.shepard.localization.enums.commands.fun;

public enum OhaLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.oha.description");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    OhaLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
