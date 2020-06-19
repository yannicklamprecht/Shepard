package de.eldoria.shepard.localization.enums.commands.moderation;

public enum RegisterPrefixLocale {
    DESCRIPTION("command.prefix.subcommand.description"),
    ADD("command.prefix.subcommand.add"),
    REMOVE("command.prefix.subcommand.remove"),
    LIST("command.prefix.subcommand.list"),
    SUCCESS_ADD("command.prefix.message.add"),
    SUCCESS_REMOVE("command.prefix.message.remove"),
    LIST_TITLE("command.prefix.message.listTitle");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    RegisterPrefixLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
