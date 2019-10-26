package de.eldoria.shepard.localization.enums.botconfig;

public enum ManageContextUserLocale {
    DESCRIPTION("command.manageContextUser.description"),
    C_SET_ACTIVE("command.manageContextUser.subcommand.setActive"),
    C_SET_LIST_TYPE("command.manageContextUser.subcommand.setListType"),
    C_ADD_USER("command.manageContextUser.subcommand.addUser"),
    C_REMOVE_USER("command.manageContextUser.subcommand.removeUser"),
    A_LIST_TYPE("command.manageContextUser.argument.listType"),
    M_ADDED_USERS("command.manageContextUser.message.addedUser"),
    M_REMOVED_USERS("command.manageContextUser.message.removedUser"),
    M_CHANGED_LIST_TYPE("command.manageContextUser.message.changedListType"),
    M_ACTIVATED_CHECK("command.manageContextUser.message.activatedCheck"),
    M_DEACTIVATED_CHECK("command.manageContextUser.message.deactivatedCheck");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    ManageContextUserLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }

}
