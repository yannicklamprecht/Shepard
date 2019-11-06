package de.eldoria.shepard.localization.enums.commands.botconfig;

public enum ContextInfoLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.contextInfo.description");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    ContextInfoLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
