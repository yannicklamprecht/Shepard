package de.chojo.shepard.database.types;

public class TicketType {
    private int id;
    private String categoryId;
    private String creationMessage;
    private String keyword;

    public TicketType(int id, String categoryId, String creationMessage, String keyword) {
        this.id = id;
        this.categoryId = categoryId;
        this.creationMessage = creationMessage;
        this.keyword = keyword;
    }

    public TicketType(String categoryId, String creationMessage, String keyword) {
        this.id = -1;
        this.categoryId = categoryId;
        this.creationMessage = creationMessage;
        this.keyword = keyword;
    }
}
