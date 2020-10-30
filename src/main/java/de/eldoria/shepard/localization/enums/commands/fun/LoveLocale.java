package de.eldoria.shepard.localization.enums.commands.fun;

public enum LoveLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.love.description"),
    /**
     * Localization key for argument say message.
     */
    OTHER("command.love.outputOther"),
    C_SOMEONE("command.love.someone"),
    C_OTHER("command.love.other")
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
    LoveLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
