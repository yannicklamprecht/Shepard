package de.eldoria.shepard.localization.enums.commands.util;

public enum SystemInfoLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.userInfo.description"),
    M_TITLE(""),
    M_AVAILABLE_CORES(""),
    M_MEMORY(""),
    M_USED_MEMORY(""),
    M_SERVICE_INFO(""),
    M_SERVERS(""),
    M_USERS("");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    SystemInfoLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
