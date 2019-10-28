package de.eldoria.shepard.localization.enums.commands.util;

public enum UserInfoLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.userInfo.description"),
    W_NICKNAME("command.userInfo.words.nickname"),
    W_STATUS("command.userInfo.words.status"),
    W_MINECRAFT_NAME("command.userInfo.words.minecraftName"),
    W_MENTION("command.userInfo.words.mention"),
    W_JOINED("command.userInfo.words.joined"),
    W_ROLES("command.userInfo.words.roles"),
    W_CREATED("command.userInfo.words.created"),
    W_UNKOWN("command.userInfo.words.unknown"),
    W_YEAR("command.userInfo.words.year"),
    W_YEARS("command.userInfo.words.years"),
    W_DAY("command.userInfo.words.day"),
    W_DAYS("command.userInfo.words.days"),
    W_MESSAGE("command.userInfo.words.message");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    UserInfoLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
