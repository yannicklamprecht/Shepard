package de.eldoria.shepard.localization.enums.commands.moderation;

public enum  BanLocale {
    DESCRIPTION("command.ban.description"),
    C_PERMA("command.ban.subcommand.perm"),
    C_TEMP("command.ban.subcommand.temp"),
    C_SOFT("command.ban.subcommand.soft"),
    A_REASON("command.ban.subcommand.argument.reason"),
    AD_REASON("command.ban.subcommand.argumentDescription.reason"),
    A_PURGE("command.ban.subcommand.argument.purge"),
    AD_PURGE("command.ban.subcommand.argumentDescription.purge"),
    A_TIME("command.ban.subcommand.argument.time"),
    M_PERM("command.ban.message.permBan"),
    M_TEMP("command.ban.message.tempBan"),
    M_SOFT("command.ban.message.softBan"),
    M_PERM_BANNED("command.ban.message.permBanned"),
    M_TEMP_BANNED("command.ban.message.tempBanned"),
    M_SOFT_BANNED("command.ban.message.softBanned");

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
