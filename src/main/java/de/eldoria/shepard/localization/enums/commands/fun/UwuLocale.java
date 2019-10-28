package de.eldoria.shepard.localization.enums.commands.fun;

public enum UwuLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.uwu.description");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    UwuLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}