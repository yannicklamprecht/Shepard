package de.eldoria.shepard.localization.enums.commands.botconfig;

public enum ManageContextGuildLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.manageContextGuild.description"),
    /**
     * Localization key for subcommand setActive.
     */
    C_SET_ACTIVE("command.manageContextGuild.subcommand.setActive"),
    /**
     * Localization key for subcommand setListType.
     */
    C_SET_LIST_TYPE("command.manageContextGuild.subcommand.setListType"),
    /**
     * Localization key for subcommand addGuild.
     */
    C_ADD_GUILD("command.manageContextGuild.subcommand.addGuild"),
    /**
     * Localization key for subcommand removeGuild.
     */
    C_REMOVE_GUILD("command.manageContextGuild.subcommand.removeGuild"),
    /**
     * Localization key for argument listType.
     */
    A_LIST_TYPE("command.manageContextGuild.argument.listType"),
    /**
     * Localization key for message added guilds.
     */
    M_ADDED_GUILDS("command.manageContextGuild.message.addedGuilds"),
    /**
     * Localization key for message removed guilds.
     */
    M_REMOVED_GUILDS("command.manageContextGuild.message.removedGuilds"),
    /**
     * Localization key for message changed list type.
     */
    M_CHANGED_LIST_TYPE("command.manageContextGuild.message.changedListType"),
    /**
     * Localization key for message activated check.
     */
    M_ACTIVATED_CHECK("command.manageContextGuild.message.activatedCheck"),
    /**
     * Localization key for message deactivated check.
     */
    M_DEACTIVATED_CHECK("command.manageContextGuild.message.deactivatedCheck");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    ManageContextGuildLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
