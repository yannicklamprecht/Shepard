package de.eldoria.shepard.localization.enums.admin;

public enum TicketSettingsLocale {
    DESCRIPTION("command.ticketSettings.description"),
    C_CREATE_TYPE("command.ticketSettings.subcommand.createType"),
    C_REMOVE_TYPE("command.ticketSettings.subcommand.removeType"),
    C_SET_OWNER_ROLES("command.ticketSettings.subcommand.setOwnerRoles"),
    C_SET_SUPPORT_ROLES("command.ticketSettings.subcommand.setSupportRoles"),
    C_SET_CATEGORY("command.ticketSettings.subcommand.setCategory"),
    C_SET_CREATION_MESSAGE("command.ticketSettings.subcommand.setCreationMessage"),
    M_CREATE_TYPE("command.ticketSettings.message.createdType"),
    M_REMOVE_TYPE("command.ticketSettings.message.removedType"),
    M_SET_OWNER_ROLES("command.ticketSettings.message.setOwnerRoles"),
    M_SET_SUPPORT_ROLES("command.ticketSettings.message.setSupportRoles"),
    M_SET_CATEGORY("command.ticketSettings.message.setCategory"),
    M_SET_CREATION_MESSAGE("command.ticketSettings.message.setCreationMessage");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    TicketSettingsLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }
}
