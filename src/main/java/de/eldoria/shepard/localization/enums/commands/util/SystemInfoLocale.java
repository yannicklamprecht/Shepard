package de.eldoria.shepard.localization.enums.commands.util;

public enum SystemInfoLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.systemInfo.description"),
    /**
     * Localization key for message title.
     */
    M_TITLE("command.systemInfo.message.title"),
    /**
     * Localization key for message cores.
     */
    M_AVAILABLE_CORES("command.systemInfo.message.availableCore"),
    /**
     * Localization key for message memory.
     */
    M_MEMORY("command.systemInfo.message.memory"),
    /**
     * Localization key for message used memory.
     */
    M_USED_MEMORY("command.systemInfo.message.usedMemory"),
    /**
     * Localization key for message service info.
     */
    M_SERVICE_INFO("command.systemInfo.message.serviceInfo"),
    /**
     * Localization key for message service info message.
     */
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
