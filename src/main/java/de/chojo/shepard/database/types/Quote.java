package de.chojo.shepard.database.types;

public class Quote {
    String quote;
    int quoteId;

    public Quote(String quote, int quoteId) {
        this.quote = quote;
        this.quoteId = quoteId;
    }

    public String getQuote() {
        return quote;
    }

    public int getQuoteId() {
        return quoteId;
    }
}
