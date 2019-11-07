package de.eldoria.shepard.localization.enums.commands.util;

public enum HomeLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.home.description"),
    /**
     * Localization key for message come on board.
     */
    M_COME_ON_BOARD("command.home.message.comeOnBoard"),
    /**
     * Localization key for message join now.
     */
    M_JOIN_NOW("command.home.message.joinNow");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     * @param localeCode locale code
     */
    HomeLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
