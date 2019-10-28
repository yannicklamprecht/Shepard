package de.eldoria.shepard.localization.enums.commands.util;

public enum HireMeLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.hireMe.description"),
    M_HIRE_ME("command.hireMe.message.hireMe"),
    M_I_WANT_YOU("command.hireMe.message.iWantyou"),
    M_TAKE_ME("command.hireMe.message.takeMe");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    HireMeLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
