package de.eldoria.shepard.localization.enums.botconfig;

public enum ContextInfoLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.contextInfo.description");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    ContextInfoLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }
}
