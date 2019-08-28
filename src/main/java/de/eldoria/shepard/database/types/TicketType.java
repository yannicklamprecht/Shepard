package de.eldoria.shepard.database.types;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;

public class TicketType {
    private final int id;
    private final Category categoryId;
    private final String creationMessage;
    private final String keyword;

    /**
     * Creates a new Ticket type object.
     *
     * @param guild           Guild for which this ticket type was created
     * @param id              id.
     * @param categoryId      channel category
     * @param creationMessage creation message
     * @param keyword         creation keyword
     */
    public TicketType(Guild guild, int id, String categoryId, String creationMessage, String keyword) {
        this.id = id;
        this.categoryId = guild.getCategoryById(categoryId);
        this.creationMessage = creationMessage;
        this.keyword = keyword;
    }

    /**
     * Creates a new Ticket type object.
     *
     * @param guild           Guild for which this ticket type was created
     * @param categoryId      channel category
     * @param creationMessage creation message
     * @param keyword         creation keyword
     */
    public TicketType(Guild guild, String categoryId, String creationMessage, String keyword) {
        this.id = -1;
        this.categoryId = guild.getCategoryById(categoryId);
        this.creationMessage = creationMessage;
        this.keyword = keyword;
    }

    /**
     * Get the id of the ticket type.
     *
     * @return ticket type id
     */
    public int getId() {
        return id;
    }

    /**
     * Get the channel category for channel creation.
     *
     * @return channel category
     */
    public Category getCategory() {
        return categoryId;
    }

    /**
     * Returns creation message, which is send when a ticket of type is created.
     *
     * @return creation message with possible placeholder
     */
    public String getCreationMessage() {
        return creationMessage;
    }

    /**
     * get the keyword and primal identifier of type.
     *
     * @return keyword as string.
     */
    public String getKeyword() {
        return keyword;
    }
}
