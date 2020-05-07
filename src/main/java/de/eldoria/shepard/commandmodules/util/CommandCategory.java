package de.eldoria.shepard.commandmodules.util;

import de.eldoria.shepard.localization.enums.commands.util.HelpLocale;

/**
 * Enum to specify the category of a context.
 */
@SuppressWarnings("CheckStyle")
public enum CommandCategory {
    /**
     * Context is a command of category bot administration.
     */
    ADMIN("⚙", HelpLocale.M_ADMIN.tag),
    /**
     * Context is a command of category bot administration.
     */
    MODERATION("🔨", HelpLocale.M_ADMIN.tag),
    /**
     * Context is a command of category bot configuration.
     */
    BOT_CONFIG("🔧", HelpLocale.M_BOT_CONFIG.tag),
    /**
     * Context is a command of category server exclusive.
     */
    EXCLUSIVE("🎉", HelpLocale.M_EXCLUSIVE.tag),
    /**
     * Context is a command of category entertainment.
     */
    FUN("🕹", HelpLocale.M_FUN.tag),
    /**
     * Context is a command of category utility.
     */
    UTIL("⁉", HelpLocale.M_UTIL.tag),
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
     * @param emoji emoji as unicode string
     * @param tag   locale tag of the category
     */
    CommandCategory(String emoji, String tag) {
        this.categoryName = emoji + " " + tag;
    }
}
