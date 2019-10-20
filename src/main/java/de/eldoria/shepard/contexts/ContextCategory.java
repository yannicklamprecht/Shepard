package de.eldoria.shepard.contexts;

public enum ContextCategory {
    /**
     * Context is a command of category bot administration.
     */
    ADMIN("\u2699 Bot Administration"),
    /**
     * Context is a command of category bot configuration.
     */
    BOTCONFIG("\uD83D\uDD27 Bot Configuration"),
    /**
     * Context is a command of category server exclusive.
     */
    EXCLUSIVE("\uD83C\uDF89 Server exclusive Command"),
    /**
     * Context is a command of category entertainment.
     */
    FUN("\uD83D\uDD79 Entertainment Commands"),
    /**
     * Context is a command of category utility.
     */
    UTIL("\u2049 Utility Commands"),
    /**
     * Context is keyword.
     */
    KEYWORD("Keywords");

    /**
     * Formatted name of the category.
     */
    public final String categoryName;

    ContextCategory(String categoryName) {
        this.categoryName = categoryName;
    }
}
