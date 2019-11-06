package de.eldoria.shepard.localization.enums.commands.fun;

public enum MockingSpongebobLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.mockingSpongebob.description");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    MockingSpongebobLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
