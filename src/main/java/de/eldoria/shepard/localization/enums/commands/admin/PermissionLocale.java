package de.eldoria.shepard.localization.enums.commands.admin;

public enum PermissionLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.permission.description"),
    /**
     * Localization key for subcommand addUser.
     */
    C_ADD_USER("command.permission.subcommand.addUser"),
    /**
     * Localization key for subcommand removeUser.
     */
    C_REMOVE_USER("command.permission.subcommand.removeUser"),
    /**
     * Localization key for subcommand listUser.
     */
    C_LIST_USER("command.permission.subcommand.listUser"),
    /**
     * Localization key for subcommand addRole.
     */
    C_ADD_ROLE("command.permission.subcommand.addRole"),
    /**
     * Localization key for subcommand removeRole.
     */
    C_REMOVE_ROLE("command.permission.subcommand.removeRole"),
    /**
     * Localization key for subcommand listRole.
     */
    C_LIST_ROLE("command.permission.subcommand.listRoles"),
    /**
     * Localization key for message user access.
     */
    M_USER_ACCESS("command.permission.message.userAccess"),
    /**
     * Localization key for message role access.
     */
    M_ROLE_ACCESS("command.permission.message.rolesAccess"),
    /**
     * Localization key for message user access granted.
     */
    M_USER_ACCESS_GRANTED("command.permission.message.userAccessGranted"),
    /**
     * Localization key for message user access revoked.
     */
    M_USER_ACCESS_REVOKED("command.permission.message.userAccessRevoked"),
    /**
     * Localization key for message role access granted.
     */
    M_ROLE_ACCESS_GRANTED("command.permission.message.roleAccessGranted"),
    /**
     * Localization key for message role access revoked.
     */
    M_ROLE_ACCESS_REVOKED("command.permission.message.roleAccessRevoked");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     * @param localeCode locale code
     */
    PermissionLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
