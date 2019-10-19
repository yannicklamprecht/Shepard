package de.eldoria.shepard.contexts;

public enum ContextCategory {
    ADMIN("Bot Administration"),
    BOTCONFIG("Bot Configuration"),
    EXCLUSIVE("Server exclusive Command"),
    FUN("Entertainment Commands"),
    UTIL("Utility Commands"),
    KEYWORD("Keywords");

    public final String category_Name;

    ContextCategory(String category_Name) {
        this.category_Name = category_Name;
    }
}
