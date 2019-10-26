package de.eldoria.shepard.localization.enums.admin;

public enum TicketLocale {
    DESCRIPTION("command.ticket.description"),
    C_OPEN("command.ticket.subcommand.open"),
    C_CLOSE("command.ticket.subcommand.close"),
    C_INFO("command.ticket.subcommand.info"),
    A_TICKET_TYPE("command.ticket.argument.ticketType"),
    A_CLOSE("command.ticket.argument.close"),
    A_INFO("command.ticket.argument.info"),
    M_OPEN("command.ticket.message.open"),
    M_TYPE_LIST("command.ticket.message.typeList"),
    M_TYPE_ABOUT("command.ticket.message.typeAbout"),
    M_CHANNEL_CATEGORY("command.ticket.message.channelCategory"),
    M_CREATION_MESSAGE("command.ticket.message.creationMessage"),
    M_TICKET_OWNER_ROLES("command.ticket.message.ticketOwnerRoles"),
    M_TICKET_SUPPORT_ROLES("command.ticket.message.ticketSupporterRoles");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    TicketLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }
}
