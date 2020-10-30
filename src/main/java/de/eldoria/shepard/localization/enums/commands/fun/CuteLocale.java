package de.eldoria.shepard.localization.enums.commands.fun;

public enum CuteLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.cute.description"),
    /**
     * Localization key for argument say message.
     */
    OTHER("command.cute.outputOther"),
    C_EMPTY("command.cute.empty"),
    C_OTHER("command.cute.other")
    ;

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    CuteLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
