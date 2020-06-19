package de.eldoria.shepard.localization.enums.commands.moderation;

public enum BadWordsLocale {
    DESCRIPTION("command.badword.description"),
    C_ADD("command.badword.subcommand.add"),
    C_REMOVE("command.badword.subcommand.remove"),
    C_LIST("command.badword.subcommand.list"),
    M_ADD("command.badword.message.add"),
    M_REMOVE("command.badword.message.remove"),
    M_LIST_TITLE("command.badword.message.listTitle");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    BadWordsLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
