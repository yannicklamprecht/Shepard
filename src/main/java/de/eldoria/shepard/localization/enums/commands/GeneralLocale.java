package de.eldoria.shepard.localization.enums.commands;

public enum GeneralLocale {
    /**
     * Localization key for argument [channel] (ID, Mention or Name).
     */
    A_CHANNEL("command.general.argument.channel"),
    A_CHANNELS("command.general.argument.channels"),
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
     * Localization key for argument [message].
     */
    A_MESSAGE("command.general.argument.message"),
    A_MESSAGE_ID("command.general.argument.messageId"),
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
     * Localization key for argument [command].
     */
    A_COMMAND_NAME("command.general.argument.contextName"),
    /**
     * Localization key for argument [Seconds] Only full Seconds.
     */
    A_SECONDS("command.general.argument.seconds"),
    /**
     * Localization kex for argument [amount] Only full positive numbers.
     */
    A_AMOUNT("command.general.argument.amount"),
    A_URL("command.general.argument.url"),
    A_KEYWORD("command.general.argument.keyword"),
    A_PERMISSION("command.general.argument.permission"),
    /**
     * Localization key for argument listType.
     */
    A_LIST_TYPE("command.general.argument.listType"),
    AD_MESSAGE_ID("command.general.argumentDescription.messageId"),
    AD_LIST_TYPE("command.general.argumentDescription.listType"),
    AD_CHANNEL("command.general.argumentDescription.channel"),
    AD_CHANNELS("command.general.argumentDescription.channels"),
    AD_CATEGORY("command.general.argumentDescription.category"),
    AD_USER("command.general.argumentDescription.user"),
    AD_USERS("command.general.argumentDescription.users"),
    AD_ROLE("command.general.argumentDescription.role"),
    AD_ROLES("command.general.argumentDescription.roles"),
    AD_GUILDS("command.general.argumentDescription.guilds"),
    AD_MESSAGE_MENTION("command.general.argumentDescription.messageMention"),
    AD_ID("command.general.argumentDescription.id"),
    /**
     * Localization key for argument [channel] (ID, Mention or name) or leave empty to use current channel.
     */
    AD_CHANNEL_MENTION_OR_EXECUTE("command.general.argumentDescription.channelMentionOrExecution"),
    AD_COMMAND_NAME("command.general.argumentDescription.contextName"),
    AD_SECONDS("command.general.argumentDescription.seconds"),
    AD_AMOUNT("command.general.argumentDescription.amount"),
    AD_KEYWORD("command.general.argumentDescription.keyword"),
    AD_PERMISSION("command.general.argumentDescription.permission"),
    /**
     * Localization key for argument [true|false].
     */
    A_BOOLEAN("command.general.argument.boolean"),
    /**
     * Localization key for argument [yes|no].
     */
    A_BOOLEAN_YES_NO("command.general.argument.booleanYesNo"),
    M_COOLDOWN("command.general.message.cooldown"),

    /**
     * Localization key for Intervalls
     */
    A_INTERVAL("command.reminder.argument.interval"),
    AD_INTERVAL("command.reminder.argumentDescription.interval");

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
