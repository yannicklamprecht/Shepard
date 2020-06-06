package de.eldoria.shepard.localization.enums.commands.moderation;

public enum  BanLocale {
    DESCRIPTION("command.ban.subcommand.description"),
    PERMA("command.ban.subcommand.perm"),
    TEMP("command.ban.subcommand.temp"),
    SOFT("command.ban.subcommand.soft"),
    A_PARAMETER_REASON("command.ban.subcommand.parameter.argument.reason"),
    AD_PARAMETER_REASON("command.ban.subcommand.parameter.argumentDescription.reason"),
    A_PARAMETER_PURGE("command.ban.subcommand.parameter.argument.purge"),
    AD_PARAMETER_PURGE("command.ban.subcommand.parameter.argumentDescription.purge"),
    A_PARAMETER_TIME("command.ban.subcommand.parameter.argument.time"),
    SUCCESS_PERM("command.ban.subcommand.message.banned.success.perm"),
    SUCCESS_TEMP("command.ban.subcommand.message.banned.success.temp"),
    SUCCESS_SOFT("command.ban.subcommand.message.banned.success.soft"),
    PERM_BANNED("command.ban.subcommand.message.banned.perm"),
    TEMP_BANNED("command.ban.subcommand.message.banned.temp"),
    SOFT_BANNED("command.ban.subcommand.message.banned.soft");

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
