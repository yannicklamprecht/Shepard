package de.eldoria.shepard.localization.enums.commands.fun;

public enum WaifuLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.waifu.description"),
    /**
     * Localization key for argument say message.
     */
    OTHER("command.waifu.outputOther"),
    C_EMPTY("command.waifu.empty"),
    C_OTHER("command.waifu.other")
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
    WaifuLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
