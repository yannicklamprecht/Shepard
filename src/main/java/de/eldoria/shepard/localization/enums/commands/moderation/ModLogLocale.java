package de.eldoria.shepard.localization.enums.commands.moderation;

public enum ModLogLocale {

    DESCRIPTION("command.modLog.description"),
    C_ENABLE("command.modLog.subcommand.enable"),
    C_DISABLE("command.modLog.subcommand.disable"),
    SHOW("command.modLog.subcommand.show"),
    M_ENABLED("command.modLog.message.enabled"),
    M_DISABLED("command.modLog.message.disabled");


    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    ModLogLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
