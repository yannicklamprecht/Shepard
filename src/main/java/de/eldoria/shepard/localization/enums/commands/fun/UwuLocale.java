package de.eldoria.shepard.localization.enums.commands.fun;

public enum UwuLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.uwu.description");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    UwuLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}