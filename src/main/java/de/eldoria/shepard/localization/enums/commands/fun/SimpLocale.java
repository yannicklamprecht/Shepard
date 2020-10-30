package de.eldoria.shepard.localization.enums.commands.fun;

public enum SimpLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.simp.description"),
    /**
     * Localization key for argument say message.
     */
    OTHER("command.simp.outputOther"),
    C_EMPTY("command.simp.empty"),
    C_OTHER("command.simp.empty")
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
    SimpLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
