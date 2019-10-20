package de.eldoria.shepard.contexts;

public enum ContextCategory {
    ADMIN("\u2699 Bot Administration"),
    BOTCONFIG("\uD83D\uDD27 Bot Configuration"),
    EXCLUSIVE("\uD83C\uDF89 Server exclusive Command"),
    FUN("\uD83D\uDD79 Entertainment Commands"),
    UTIL("\u2049 Utility Commands"),
    KEYWORD("Keywords");

    public final String category_Name;

    ContextCategory(String category_Name) {
        this.category_Name = category_Name;
    }
}
