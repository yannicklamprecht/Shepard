package de.eldoria.shepard.localization.enums.commands.admin;

public enum ManageQuoteLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.manageQuote.description"),
    /**
     * Localization key for subcommand add.
     */
    C_ADD("command.manageQuote.subcommand.add"),
    /**
     * Localization key for subcommand alter.
     */
    C_ALTER("command.manageQuote.subcommand.alter"),
    /**
     * Localization key for subcommand remove.
     */
    C_REMOVE("command.manageQuote.subcommand.remove"),
    /**
     * Localization key for subcommand remove.
     */
    C_IMPORT("command.manageQuote.subcommand.import"),
    /**
     * Localization key for subcommand list.
     */
    C_LIST("command.manageQuote.subcommand.list"),
    /**
     * Localization key for argument keyword.
     */
    A_KEYWORD("command.manageQuote.argument.keyWord"),
    AD_KEYWORD("command.manageQuote.argumentDescription.keyWord"),
    /**
     * Localization key for message keyword.
     */
    M_CHANGED_QUOTE("command.manageQuote.message.changedQuote"),
    /**
     * Localization key for message removed quote.
     */
    M_REMOVED_QUOTE("command.manageQuote.message.removedQuote"),
    /**
     * Localization key for message saved quote.
     */
    M_SAVED_QUOTE("command.manageQuote.message.savedQuote"),
    /**
     * Localization key for message no quotes.
     */
    M_NO_QUOTES("command.manageQuote.message.noQuotes"),
    M_IMPORTED("command.manageQuote.message.imported");
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    ManageQuoteLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
