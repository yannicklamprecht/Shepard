package de.eldoria.shepard.localization.enums.commands.admin;

public enum RepeatCommandLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.repeatCommand.description");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    RepeatCommandLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
