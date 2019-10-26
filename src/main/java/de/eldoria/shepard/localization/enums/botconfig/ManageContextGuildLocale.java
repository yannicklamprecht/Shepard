package de.eldoria.shepard.localization.enums.botconfig;

public enum ManageContextGuildLocale {
    DESCRIPTION("command.manageContextGuild.description"),
    C_SET_ACTIVE("command.manageContextGuild.subcommand.setActive"),
    C_SET_LIST_TYPE("command.manageContextGuild.subcommand.setListType"),
    C_ADD_GUILD("command.manageContextGuild.subcommand.addGuild"),
    C_REMOVE_GUILD("command.manageContextGuild.subcommand.removeGuild"),
    A_LIST_TYPE("command.manageContextGuild.argument.listType"),
    M_ADDED_GUILDS("command.manageContextGuild.message.addedGuilds"),
    M_REMOVED_GUILDS("command.manageContextGuild.message.removedGuilds"),
    M_CHANGED_LIST_TYPE("command.manageContextGuild.message.changedListType"),
    M_ACTIVATED_CHECK("command.manageContextGuild.message.activatedCheck"),
    M_DEACTIVATED_CHECK("command.manageContextGuild.message.deactivatedCheck");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    ManageContextGuildLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }
}
