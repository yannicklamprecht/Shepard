package de.eldoria.shepard.localization.enums.commands.moderation;

public enum ModLogLocale {

    DESCRIPTION("command.modLog.subcommand.description"),
    ENABLE("command.modLog.subcommand.enable"),
    DISABLE("command.modLog.subcommand.disable"),
    SHOW("command.modLog.subcommand.show"),
    SUCCESS_ENABLED("command.modLog.message.success.enabled"),
    SUCCESS_DISABLED("command.modLog.message.success.disabled");


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
