package de.eldoria.shepard.localization.enums.commands.admin;

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
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     * @param localeCode locale code
     */
    PrefixLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
