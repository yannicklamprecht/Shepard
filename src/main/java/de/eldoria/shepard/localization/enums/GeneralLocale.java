package de.eldoria.shepard.localization.enums;

public enum GeneralLocale {
    A_EMPTY("command.general.argument.empty"),
    A_CHANNEL("command.general.argument.channel"),
    A_CATEGORY("command.general.argument.category"),
    A_USER("command.general.argument.user"),
    A_USERS("command.general.argument.users"),
    A_ROLE("command.general.argument.role"),
    A_ROLES("command.general.argument.roles"),
    A_GUILDS("command.general.argument.guilds"),
    A_MESSAGE("command.general.argument.message"),
    A_TEXT("command.general.argument.text"),
    A_MESSAGE_MENTION("command.general.argument.messageMention"),
    A_CHANNEL_MENTION_OR_EXECUTE("command.general.argument.channelMentionOrExecution"),
    A_BOOLEAN("command.general.argument.boolean"),
    A_BOOLEAN_YES_NO("command.general.argument.booleanYesNo"),
    A_ID("command.general.argument.id"),
    A_NAME("command.general.argument.name"),
    A_CONTEXT_NAME("command.general.argument.contextName");

    public final String localeCode;
    public final String replacement;

    GeneralLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }
}
