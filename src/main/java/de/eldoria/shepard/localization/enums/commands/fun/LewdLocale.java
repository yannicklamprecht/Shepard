package de.eldoria.shepard.localization.enums.commands.fun;

public enum LewdLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.pure.description"),
    /**
     * Localization key for argument say message.
     */
    PURE("command.pure.pure"),
    LEWD("command.pure.lewd"),
    C_EMPTY("command.pure.empty"),
    C_OTHER("command.pure.other");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    LewdLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
