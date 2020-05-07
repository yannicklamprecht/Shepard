package de.eldoria.shepard.localization.enums.commands.fun;

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
    M_NO_QUOTE_FOUND("command.quote.message.noQuoteFound"),
    M_NO_QUOTE_DEFINED("command.quote.message.noQuoteDefined");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    QuoteLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
