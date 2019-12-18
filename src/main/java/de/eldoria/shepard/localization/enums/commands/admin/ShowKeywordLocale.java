package de.eldoria.shepard.localization.enums.commands.admin;

public enum ShowKeywordLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.showKeywords.description");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    ShowKeywordLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }


}
