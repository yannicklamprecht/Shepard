package de.eldoria.shepard.localization.enums.commands.admin;

public enum ShowKeywordLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.showKeywords.description");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    ShowKeywordLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }


}
