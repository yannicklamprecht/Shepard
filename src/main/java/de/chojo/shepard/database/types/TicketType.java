package de.chojo.shepard.database.types;

public class TicketType {
    private final int id;
    private final String categoryId;
    private final String creationMessage;
    private final String keyword;

    /**
     * Creates a new Ticket type object.
     *
     * @param id id.
     * @param categoryId channel category
     * @param creationMessage creation message
     * @param keyword creation keyword
     */
    public TicketType(int id, String categoryId, String creationMessage, String keyword) {
        this.id = id;
        this.categoryId = categoryId;
        this.creationMessage = creationMessage;
        this.keyword = keyword;
    }

    /**
     * Creates a new Ticket type object
     *
     * @param categoryId channel category
     * @param creationMessage creation message
     * @param keyword creation keyword
     */
    public TicketType(String categoryId, String creationMessage, String keyword) {
        this.id = -1;
        this.categoryId = categoryId;
        this.creationMessage = creationMessage;
        this.keyword = keyword;
    }


}
