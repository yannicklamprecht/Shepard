package de.eldoria.shepard.localization.enums;

public enum ManageQuoteLocale {
    C_ADD("command.manageQuote.subcommand.add"),
    C_ALTER("command.manageQuote.subcommand.alter"),
    C_REMOVE("command.manageQuote.subcommand.remove"),
    C_LIST("command.manageQuote.subcommand.list"),
    A_QUOTE_ID("command.manageQuote.argument.quoteId"),
    A_KEYWORD("command.manageQuote.argument.keyWord"),
    M_CHANGED_QUOTE("command.manageQuote.message.changedQuote"),
    M_REMOVED_QUOTE("command.manageQuote.message.removedQuote"),
    M_SAVED_QUOTE("command.manageQuote.message.savedQuote"),
    M_NO_QUOTES("command.manageQuote.message.noQuotes");

    public final String localeCode;
    public final String replacement;

    ManageQuoteLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }

}
