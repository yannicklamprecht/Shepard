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
     * Localization key for subcommand list.
     */
    C_LIST("command.manageQuote.subcommand.list"),
    /**
     * Localization key for argument quote id.
     */
    A_QUOTE_ID("command.manageQuote.argument.quoteId"),
    /**
     * Localization key for argument keyword.
     */
    A_KEYWORD("command.manageQuote.argument.keyWord"),
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
    M_NO_QUOTES("command.manageQuote.message.noQuotes");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    ManageQuoteLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
