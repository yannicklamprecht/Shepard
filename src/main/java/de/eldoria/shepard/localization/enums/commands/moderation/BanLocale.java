package de.eldoria.shepard.localization.enums.commands.moderation;

public enum  BanLocale {
    DESCRIPTION("command.ban.description"),
    PERMA("command.ban.subcommand.perm"),
    TEMP("command.ban.subcommand.temp"),
    SOFT("command.ban.subcommand.soft"),
    A_PARAMETER_REASON("command.ban.subcommand.parameter.argument.reason"),
    AD_PARAMETER_REASON("command.ban.subcommand.parameter.argumentDescription.reason"),
    A_PARAMETER_PURGE("command.ban.subcommand.parameter.argument.purge"),
    AD_PARAMETER_PURGE("command.ban.subcommand.parameter.argumentDescription.purge"),
    A_PARAMETER_TIME("command.ban.subcommand.parameter.argument.time"),
    SUCCESS_PERM("command.ban.message.perm.success"),
    SUCCESS_TEMP("command.ban.message.temp.success"),
    SUCCESS_SOFT("command.ban.message.soft.success"),
    PERM_BANNED("command.ban.message.perm"),
    TEMP_BANNED("command.ban.message.temp"),
    SOFT_BANNED("command.ban.message.soft");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    BanLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
