package de.eldoria.shepard.commandmodules.util;

/**
 * Enum to specify the category of a context.
 */
@SuppressWarnings("CheckStyle")
public enum CommandCategory {
    /**
     * Context is a command of category bot administration.
     */
    ADMIN("⚙", "command.help.message.admin"),
    /**
     * Context is a command of category bot administration.
     */
    MODERATION("🔨", "command.help.message.moderation"),
    /**
     * Context is a command of category bot configuration.
     */
    BOT_CONFIG("🔧", "command.help.message.botConfig"),
    /**
     * Context is a command of category server exclusive.
     */
    EXCLUSIVE("🎉", "command.help.message.exclusive"),
    /**
     * Context is a command of category entertainment.
     */
    FUN("🕹", "command.help.message.fun"),
    /**
     * Context is a command of category utility.
     */
    UTIL("⁉", "command.help.message.util"),
    /**
     * Context is a command of category utility.
     */
    REACTION("😏", "command.help.message.reactions");

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
        this.categoryName = emoji + " $" + tag + "$";
    }
}
