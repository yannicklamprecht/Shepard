package de.eldoria.shepard.localization.enums.commands.util;

public enum SystemInfoLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.systemInfo.description"),
    M_TITLE("command.systemInfo.message.title"),
    M_AVAILABLE_CORES("command.systemInfo.message.availableCore"),
    M_MEMORY("command.systemInfo.message.memory"),
    M_USED_MEMORY("command.systemInfo.message.usedMemory"),
    M_SERVICE_INFO("command.systemInfo.message.serviceInfo"),
    M_SERVICE_INFO_MESSAGE("command.systemInfo.message.serviceInfoText");

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
