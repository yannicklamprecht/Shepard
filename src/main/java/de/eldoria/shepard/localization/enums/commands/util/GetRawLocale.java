package de.eldoria.shepard.localization.enums.commands.util;

public enum GetRawLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.getRaw.description");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    GetRawLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
