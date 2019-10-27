package de.eldoria.shepard.localization.enums.admin;

public enum PrefixLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.prefix.description"),
    /**
     * Localization key for subcommand set.
     */
    C_SET("command.prefix.subcommand.set"),
    /**
     * Localization key for subcommand reset.
     */
    C_RESET("command.prefix.subcommand.reset"),
    /**
     * Localization key for argument prefix format.
     */
    A_PREFIX_FORMAT("command.prefix.subcommand.prefixFormat"),
    /**
     * Localization key for message changed.
     */
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
