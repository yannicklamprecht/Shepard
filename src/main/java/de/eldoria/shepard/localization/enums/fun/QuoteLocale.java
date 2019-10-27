package de.eldoria.shepard.localization.enums.fun;

public enum QuoteLocale {
    DESCRIPTION("command.quote.description"),
    A_EMPTY_OR_WORD("command.quote.argument.emptyOrWord"),
    M_NO_QUOTE_FOUND("command.quote.message.noQuoteFound");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    QuoteLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }

}
