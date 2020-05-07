package de.eldoria.shepard.localization.enums.commands.admin;

public enum PermissionLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.permission.description"),
    /**
     * Localization key for subcommand addUser.
     */
    C_GRANT("command.permission.subcommand.grant"),
    /**
     * Localization key for subcommand removeUser.
     */
    C_REVOKE("command.permission.subcommand.revoke"),
    /**
     * Localization key for subcommand listUser.
     */
    C_ACCESS_LIST("command.permission.subcommand.accessList"),
    A_ROLE_AND_OR_USER("command.permission.argument.userAndOrRole"),
    AD_ROLE_AND_OR_USER("command.permission.argumentDescription.userAndOrRole"),
    /**
     * Localization key for message user access.
     */
    M_USER_ACCESS("command.permission.message.userAccess"),
    /**
     * Localization key for message role access.
     */
    M_ROLE_ACCESS("command.permission.message.rolesAccess"),
    M_ACCESS_GRANTED("command.permission.message.accessGranted"),
    M_ACCESS_REVOKED("command.permission.message.accessRevoked"),
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
    M_ROLE_ACCESS_REVOKED("command.permission.message.roleAccessRevoked"),
    C_SET_PERMISSION_OVERRIDE("command.permission.subcommand.setPermissionOverride"),
    C_INFO("command.permission.subcommand.info"),
    M_OVERRIDE_ACTIVATED("command.permission.message.overrideActivated"),
    M_OVERRIDE_DEACTIVATED("command.permission.message.overrideDeactivated"),
    M_PERMISSION_REQUIRED_MESSAGE("command.permission.message.permissionNeeded"),
    M_PERMISSION_NOT_REQUIRED_MESSAGE("command.permission.message.permissionNotNeeded"),
    M_INFO_TITLE("command.permission.message.infoTitle"),
    M_PERMISSION_REQUIRED("command.permission.message.permissionRequired"),
    M_ROLES_WITH_PERMISSION("command.permission.message.rolesWithPermission"),
    M_USER_WITH_PERMISSION("command.permission.message.userWithPermission"),
    M_CLICK_EXPLANATION("command.permission.message.clickExplanation"),
    M_FULL_ACCESS("command.permission.message.fullAccess"),
    M_STANDALONE_COMMAND("command.permission.message.standaloneCommand"),
    M_SUB_COMMAND("command.permission.message.subCommand"),
    M_SUB_COMMAND_PERMISSION_OVERRIDEN("command.permission.message.subcommandPermissionOverriden"),
    M_SUB_COMMAND_PERMISSION_NEEDED("command.permission.message.subCommandPermissionNeeded"),
    M_SUB_COMMAND_PERMISSION_NOT_NEEDED("command.permission.message.subCommandPermissionNotNeeded"),
    E_PERMISSION_OVERRIDE("command.permission.error.permissionOverride");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
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
