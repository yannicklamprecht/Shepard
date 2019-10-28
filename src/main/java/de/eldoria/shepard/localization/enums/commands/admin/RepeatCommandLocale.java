package de.eldoria.shepard.localization.enums.commands.admin;

public enum RepeatCommandLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.repeatCommand.description");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    RepeatCommandLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
