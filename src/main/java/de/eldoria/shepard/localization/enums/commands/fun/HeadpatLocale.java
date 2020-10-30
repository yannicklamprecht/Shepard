package de.eldoria.shepard.localization.enums.commands.fun;

public enum HeadpatLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.headpat.description"),
    /**
     * Localization key for argument say message.
     */
    SOMEONE("command.headpat.outputOther"),
    C_SOMEONE("command.headpat.someone");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    HeadpatLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
