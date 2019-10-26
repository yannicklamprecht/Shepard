package de.eldoria.shepard.localization.enums.admin;

public enum PermissionLocale {
    DESCRIPTION("command.permission.description"),
    C_ADD_USER("command.permission.subcommand.addUser"),
    C_REMOVE_USER("command.permission.subcommand.removeUser"),
    C_LIST_USER("command.permission.subcommand.listUser"),
    C_ADD_ROLE("command.permission.subcommand.addRole"),
    C_REMOVE_ROLE("command.permission.subcommand.removeRole"),
    C_LIST_ROLE("command.permission.subcommand.listRoles"),
    M_USER_ACCESS("command.permission.message.userAccess"),
    M_ROLE_ACCESS("command.permission.message.rolesAccess"),
    M_USER_ACCESS_GRANTED("command.permission.message.userAccessGranted"),
    M_USER_ACCESS_REVOKED("command.permission.message.userAccessRevoked"),
    M_ROLE_ACCESS_GRANTED("command.permission.message.roleAccessGranted"),
    M_ROLE_ACCESS_REVOKED("command.permission.message.roleAccessRevoked");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    PermissionLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }
}
