package de.eldoria.shepard.localization.enums.admin;

public enum PrefixLocale {
    DESCRIPTION("command.prefix.description"),
    C_SET("command.prefix.subcommand.set"),
    C_RESET("command.prefix.subcommand.reset"),
    A_PREFIX_FORMAT("command.prefix.subcommand.prefixFormat"),
    M_CHANGED("command.prefix.message.changed");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    PrefixLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }
}
