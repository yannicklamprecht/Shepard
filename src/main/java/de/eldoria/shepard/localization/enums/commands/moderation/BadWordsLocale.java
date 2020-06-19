package de.eldoria.shepard.localization.enums.commands.moderation;

public enum BadWordsLocale {
    DESCRIPTION("command.badword.description"),
    ADD("command.badword.subcommand.add"),
    REMOVE("command.badword.subcommand.remove"),
    LIST("command.badword.subcommand.list"),
    SUCCESS_ADD("command.badword.message.success.add"),
    SUCCESS_REMOVE("command.badword.message.success.remove"),
    LIST_TITLE("command.badword.message.list.title");

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
