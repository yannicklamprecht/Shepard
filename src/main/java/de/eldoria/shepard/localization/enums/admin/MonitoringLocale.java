package de.eldoria.shepard.localization.enums.admin;

public enum MonitoringLocale {
    DESCRIPTION("command.monitoring.description"),
    C_ADD("command.monitoring.subcommand.add"),
    C_REMOVE("command.monitoring.subcommand.remove"),
    C_LIST("command.monitoring.subcommand.list"),
    C_ENABLE("command.monitoring.subcommand.enable"),
    C_DISABLE("command.monitoring.subcommand.disable"),
    A_ADDRESS("command.monitoring.argument.address"),
    A_ADD_TEXT("command.monitoring.argument.addText"),
    M_REGISTERED_ADDRESS("command.monitoring.message.registeredAddress"),
    M_REGISTERED_CHANNEL("command.monitoring.message.registeredChannel"),
    M_REMOVED_ADDRESS("command.monitoring.message.removedAddress"),
    M_REMOVED_CHANNEL("command.monitoring.message.removedChannel"),
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
