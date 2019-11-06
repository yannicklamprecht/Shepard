package de.eldoria.shepard.localization.enums.commands.util;

public enum UserInfoLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.userInfo.description"),
    /**
     * Localization key for message .
     */
    M_JOINED("command.userInfo.message.joined"),
    /**
     * Localization key for message .
     */
    M_CREATED("command.userInfo.message.created"),
    /**
     * Localization key for word "Nickname".
     */
    W_NICKNAME("command.userInfo.words.nickname"),
    /**
     * Localization key for word "Status".
     */
    W_STATUS("command.userInfo.words.status"),
    /**
     * Localization key for word "Minecraft Name".
     */
    W_MINECRAFT_NAME("command.userInfo.words.minecraftName"),
    /**
     * Localization key for word "Mention".
     */
    W_MENTION("command.userInfo.words.mention"),
    /**
     * Localization key for word "Joined".
     */
    W_JOINED("command.userInfo.words.joined"),
    /**
     * Localization key for word "Roles".
     */
    W_ROLES("command.userInfo.words.roles"),
    /**
     * Localization key for word "Unkown".
     */
    W_UNKOWN("command.userInfo.words.unknown"),
    /**
     * Localization key for word "Year".
     */
    W_YEAR("command.userInfo.words.year"),
    /**
     * Localization key for word "Years".
     */
    W_YEARS("command.userInfo.words.years"),
    /**
     * Localization key for word "Year".
     */
    W_MONTH("command.userInfo.words.month"),
    /**
     * Localization key for word "Years".
     */
    W_MONTHS("command.userInfo.words.months"),
    /**
     * Localization key for word "Day".
     */
    W_DAY("command.userInfo.words.day"),
    /**
     * Localization key for word "Days".
     */
    W_DAYS("command.userInfo.words.days");

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
