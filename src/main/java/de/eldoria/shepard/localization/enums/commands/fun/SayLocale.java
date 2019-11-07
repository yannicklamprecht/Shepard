package de.eldoria.shepard.localization.enums.commands.fun;

public enum SayLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.say.description"),
    /**
     * Localization key for argument say message.
     */
    A_SAY("command.say.argument.say");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     * @param localeCode locale code
     */
    SayLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
