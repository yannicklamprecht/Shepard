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
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    HomeLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
