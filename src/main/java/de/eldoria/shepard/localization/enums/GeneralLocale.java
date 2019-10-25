package de.eldoria.shepard.localization.enums;

public enum GeneralLocale {
    EMPTY("command.general.empty"),
    CHANNEL("command.general.channel"),
    ROLE("command.general.role"),
    MESSAGE("command.general.message"),
    MESSAGE_MENTION("command.general.messageMention"),
    CHANNEL_MENTION_OR_EXECUTE("command.general.channelMentionOrExecution");

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
