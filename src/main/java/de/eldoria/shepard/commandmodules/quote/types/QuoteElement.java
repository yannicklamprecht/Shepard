package de.eldoria.shepard.commandmodules.quote.types;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class QuoteElement {
    private final String quote;
    private final int quoteId;
    private final String source;
    private final LocalDateTime created;
    private final LocalDateTime edited;

    /**
     * Creates new Quote object.
     *  @param quote   quote
     * @param quoteId id of quote
     * @param source who is mentioned in this quote
     * @param created time when the quote was saved
     */
    public QuoteElement(String quote, int quoteId, String source, LocalDateTime created, LocalDateTime edited) {
        this.quote = quote;
        this.quoteId = quoteId;
        this.source = source;
        this.created = created;
        this.edited = edited;
    }
}
