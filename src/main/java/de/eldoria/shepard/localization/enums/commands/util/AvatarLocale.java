package de.eldoria.shepard.localization.enums.commands.util;

public enum AvatarLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.avatar.description"),
    C_DEFAULT("command.avatar.command.default"),
    C_OTHER("command.avatar.command.other"),
    M_AVATAR("command.avatar.message.avatar");
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    AvatarLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }


}
