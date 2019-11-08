package de.eldoria.shepard.contexts;

import de.eldoria.shepard.localization.enums.commands.util.HelpLocale;

public enum ContextCategory {
    /**
     * Context is a command of category bot administration.
     */
    ADMIN("\u2699", HelpLocale.M_ADMIN.tag),
    /**
     * Context is a command of category bot configuration.
     */
    BOT_CONFIG("\uD83D\uDD27", HelpLocale.M_BOT_CONFIG.tag),
    /**
     * Context is a command of category server exclusive.
     */
    EXCLUSIVE("\uD83C\uDF89", HelpLocale.M_EXCLUSIVE.tag),
    /**
     * Context is a command of category entertainment.
     */
    FUN("\uD83D\uDD79", HelpLocale.M_FUN.tag),
    /**
     * Context is a command of category utility.
     */
    UTIL("\u2049", HelpLocale.M_UTIL.tag),
    /**
     * Context is keyword.
     */
    KEYWORD("", "Keywords");

    /**
     * Formatted name of the category.
     */
    public final String categoryName;

    /**
     * Creates a new context category.
     *
     * @param tag locale tag of the category
     */
    ContextCategory(String emoji, String tag) {
        this.categoryName = emoji + " " + tag;
    }
}
