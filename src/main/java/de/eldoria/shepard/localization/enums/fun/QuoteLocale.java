package de.eldoria.shepard.localization.enums.fun;

public enum QuoteLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.quote.description"),
    /**
     * Localization key for argument empty or word.
     */
    A_EMPTY_OR_WORD("command.quote.argument.emptyOrWord"),
    /**
     * Localization key for message no quote found.
     */
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
