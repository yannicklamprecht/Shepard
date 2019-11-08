package de.eldoria.shepard.localization.enums.commands.botconfig;

public enum ManageContextUserLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.manageContextUser.description"),
    /**
     * Localization key for subcommand setActive.
     */
    C_SET_ACTIVE("command.manageContextUser.subcommand.setActive"),
    /**
     * Localization key for subcommand setListType.
     */
    C_SET_LIST_TYPE("command.manageContextUser.subcommand.setListType"),
    /**
     * Localization key for subcommand addUser.
     */
    C_ADD_USER("command.manageContextUser.subcommand.addUser"),
    /**
     * Localization key for subcommand removeUser.
     */
    C_REMOVE_USER("command.manageContextUser.subcommand.removeUser"),
    /**
     * Localization key for argument listType.
     */
    A_LIST_TYPE("command.manageContextUser.argument.listType"),
    /**
     * Localization key for message added users.
     */
    M_ADDED_USERS("command.manageContextUser.message.addedUser"),
    /**
     * Localization key for message removed users.
     */
    M_REMOVED_USERS("command.manageContextUser.message.removedUser"),
    /**
     * Localization key for message changed list type.
     */
    M_CHANGED_LIST_TYPE("command.manageContextUser.message.changedListType"),
    /**
     * Localization key for message activated check.
     */
    M_ACTIVATED_CHECK("command.manageContextUser.message.activatedCheck"),
    /**
     * Localization key for message deactivated check.
     */
    M_DEACTIVATED_CHECK("command.manageContextUser.message.deactivatedCheck");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    ManageContextUserLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
