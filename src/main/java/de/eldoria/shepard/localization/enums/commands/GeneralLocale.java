package de.eldoria.shepard.localization.enums.commands;

public enum GeneralLocale {
    /**
     * Localization key for argument leave empty.
     */
    A_EMPTY("command.general.argument.empty"),
    /**
     * Localization key for argument [channel] (ID, Mention or Name).
     */
    A_CHANNEL("command.general.argument.channel"),
    /**
     * Localization key for argument [category ID].
     */
    A_CATEGORY("command.general.argument.category"),
    /**
     * Localization key for argument [user] (ID, Mention, Tag or Name).
     */
    A_USER("command.general.argument.user"),
    /**
     * Localization key for argument [user...] one or more user. (ID, Mention, Tag or Name).
     */
    A_USERS("command.general.argument.users"),
    /**
     * Localization key for argument [role] (ID, Mention or Name).
     */
    A_ROLE("command.general.argument.role"),
    /**
     * Localization key for argument [roles...] one or more roles. (ID, Mention or Name).
     */
    A_ROLES("command.general.argument.roles"),
    /**
     * Localization key for argument [guilds...] one or more guilds. (ID only).
     */
    A_GUILDS("command.general.argument.guilds"),
    /**
     * Localization key for argument [message] \n Supported Placeholders: {user_tag} {user_name} {user_mention}.
     */
    A_MESSAGE_MENTION("command.general.argument.messageMention"),
    /**
     * Localization key for argument [text].
     */
    A_TEXT("command.general.argument.text"),
    /**
     * Localization key for argument [name].
     */
    A_NAME("command.general.argument.name"),
    /**
     * Localization key for argument [id | Use list command to see Ids].
     */
    A_ID("command.general.argument.id"),
    /**
     * Localization key for argument [channel] (ID, Mention or name) or leave empty to use current channel.
     */
    A_CHANNEL_MENTION_OR_EXECUTE("command.general.argument.channelMentionOrExecution"),
    /**
     * Localization key for argument [Use the command name or an alias of the command].
     */
    A_CONTEXT_NAME("command.general.argument.contextName"),
    /**
     * Localization key for argument [Seconds] Only full Seconds.
     */
    A_SECONDS("command.general.argument.seconds"),
    /**
     * Localization key for argument [true|false].
     */
    A_BOOLEAN("command.general.argument.boolean"),
    /**
     * Localization key for argument [yes|no].
     */
    A_BOOLEAN_YES_NO("command.general.argument.booleanYesNo"),
    M_COOLDOWN("command.general.message.cooldown");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    GeneralLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
