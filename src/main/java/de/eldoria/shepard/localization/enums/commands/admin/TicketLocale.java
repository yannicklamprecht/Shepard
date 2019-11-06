package de.eldoria.shepard.localization.enums.commands.admin;

public enum TicketLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.ticket.description"),
    /**
     * Localization key for subcommand open.
     */
    C_OPEN("command.ticket.subcommand.open"),
    /**
     * Localization key for subcommand close.
     */
    C_CLOSE("command.ticket.subcommand.close"),
    /**
     * Localization key for subcommand info.
     */
    C_INFO("command.ticket.subcommand.info"),
    /**
     * Localization key for argument ticketType.
     */
    A_TICKET_TYPE("command.ticket.argument.ticketType"),
    /**
     * Localization key for argument close.
     */
    A_CLOSE("command.ticket.argument.close"),
    /**
     * Localization key for argument info.
     */
    A_INFO("command.ticket.argument.info"),
    /**
     * Localization key for open.
     */
    M_OPEN("command.ticket.message.open"),
    /**
     * Localization key type list.
     */
    M_TYPE_LIST("command.ticket.message.typeList"),
    /**
     * Localization key for message type about.
     */
    M_TYPE_ABOUT("command.ticket.message.typeAbout"),
    /**
     * Localization key for message channel category.
     */
    M_CHANNEL_CATEGORY("command.ticket.message.channelCategory"),
    /**
     * Localization key for message creation message.
     */
    M_CREATION_MESSAGE("command.ticket.message.creationMessage"),
    /**
     * Localization key for message ticket owner roles.
     */
    M_TICKET_OWNER_ROLES("command.ticket.message.ticketOwnerRoles"),
    /**
     * Localization key for message ticket support roles.
     */
    M_TICKET_SUPPORT_ROLES("command.ticket.message.ticketSupporterRoles");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    TicketLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
