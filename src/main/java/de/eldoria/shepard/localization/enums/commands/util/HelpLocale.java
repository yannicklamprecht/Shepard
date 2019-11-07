package de.eldoria.shepard.localization.enums.commands.util;

public enum HelpLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.help.description"),
    /**
     * Localization key for argument command name.
     */
    A_COMMAND("command.help.subcommand.command"),
    /**
     * Localization key for message usage.
     */
    M_USAGE("command.help.message.usage"),
    /**
     * Localization key for message list commands.
     */
    M_LIST_COMMANDS("command.help.message.listCommand"),
    /**
     * Localization key for message bot config.
     */
    M_BOT_CONFIG("command.help.message.botConfig"),
    /**
     * Localization key for message admin.
     */
    M_ADMIN("command.help.message.admin"),
    /**
     * Localization key for message exclusive.
     */
    M_EXCLUSIVE("command.help.message.exclusive"),
    /**
     * Localization key for message fun.
     */
    M_FUN("command.help.message.fun"),
    /**
     * Localization key for message util.
     */
    M_UTIL("command.help.message.util"),
    /**
     * Localization key for message maybe useful.
     */
    M_MAYBE_USEFUL("command.help.message.maybeUseful"),
    /**
     * Localization key for message invite me.
     */
    M_INVITE_ME("command.help.message.inviteMe"),
    /**
     * Localization key for message support server.
     */
    M_SUPPORT_SERVER("command.help.message.supportServer"),
    /**
     * Localization key for message commands.
     */
    M_COMMANDS("command.help.message.commands"),
    /**
     * Localization key for message help for command.
     */
    M_HELP_FOR_COMMAND("command.help.message.helpForCommand"),
    /**
     * Localization key for word "Arguments".
     */
    W_ARGUMENTS("command.help.word.arguments"),
    /**
     * Localization key for word "Usage".
     */
    W_USAGE("command.help.word.usage"),
    /**
     * Localization key for word "Required".
     */
    W_REQUIRED("command.help.word.required"),
    /**
     * Localization key for word "Optional".
     */
    W_OPTIONAL("command.help.word.optional"),
    /**
     * Localization key for word "aliases".
     */
    W_ALIASES("command.help.word.aliases");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     * @param localeCode locale code
     */
    HelpLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
