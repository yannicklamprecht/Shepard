package de.eldoria.shepard.localization.enums;

public enum WordsLocale {
    /**
     * Localization key for word "Name".
     */
    NAME("words.name"),
    /**
     * Localization key for word "ID".
     */
    ID("words.id"),
    /**
     * Localization key for word "Address".
     */
    ADDRESS("words.address"),
    /**
     * Localization key for word "Minecraft".
     */
    MINECRAFT("words.minecraft"),
    /**
     * Localization key for word "Context Name".
     */
    CONTEXT_NAME("words.contextName"),
    /**
     * Localization key for word "Keywords".
     */
    KEYWORDS("words.keywords"),
    /**
     * Localization key for word "Keyword".
     */
    KEYWORD("words.keyword"),
    /**
     * Localization key for word "Keyword".
     */
    MESSAGE("words.message"),
    /**
     * Localization key for word "Keyword".
     */
    TIME("words.time"),
    /**
     * Localization key for word "Category".
     */
    CATEGORY("words.category"),
    /**
     * Localization key for word "Point".
     */
    M_POINT("command.home.message.comeOnBoard"),
    /**
     * Localization key for word "Points".
     */
    M_POINTS("command.home.message.comeOnBoard");


    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    WordsLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
