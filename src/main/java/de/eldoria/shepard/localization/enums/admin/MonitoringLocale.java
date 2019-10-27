package de.eldoria.shepard.localization.enums.admin;

public enum MonitoringLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.monitoring.description"),
    /**
     * Localization key for subcommand add.
     */
    C_ADD("command.monitoring.subcommand.add"),
    /**
     * Localization key for subcommand remove.
     */
    C_REMOVE("command.monitoring.subcommand.remove"),
    /**
     * Localization key for subcommand list.
     */
    C_LIST("command.monitoring.subcommand.list"),
    /**
     * Localization key for subcommand enable.
     */
    C_ENABLE("command.monitoring.subcommand.enable"),
    /**
     * Localization key for subcommand disable.
     */
    C_DISABLE("command.monitoring.subcommand.disable"),
    /**
     * Localization key for argument address.
     */
    A_ADDRESS("command.monitoring.argument.address"),
    /**
     * Localization key for argument add text.
     */
    A_ADD_TEXT("command.monitoring.argument.addText"),
    /**
     * Localization key for message add registered address.
     */
    M_REGISTERED_ADDRESS("command.monitoring.message.registeredAddress"),
    /**
     * Localization key for message registered channel.
     */
    M_REGISTERED_CHANNEL("command.monitoring.message.registeredChannel"),
    /**
     * Localization key for message removed address.
     */
    M_REMOVED_ADDRESS("command.monitoring.message.removedAddress"),
    /**
     * Localization key for message removed channel.
     */
    M_REMOVED_CHANNEL("command.monitoring.message.removedChannel"),
    /**
     * Localization key for message registered addresses.
     */
    M_REGISTERED_ADDRESSES("command.monitoring.message.registeredAddresses");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    MonitoringLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }
}
