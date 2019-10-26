package de.eldoria.shepard.localization.enums;

public enum WordsLocale {
    NAME("words.name"),
    ID("words.id"),
    ADDRESS("words.address"),
    MINECRAFT("words.minecraft"),
    CONTEXT_NAME("words.contextName"),
    KEYWORDS("words.keywords"),
    KEYWORD("words.keyword"),
    CATEGORY("words.category");

    public final String localeCode;
    public final String replacement;

    WordsLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }
}
