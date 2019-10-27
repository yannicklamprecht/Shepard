package de.eldoria.shepard.localization.enums.fun;

public enum MockingSpongebobLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.mockingSpongebob.description");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    MockingSpongebobLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }
}
