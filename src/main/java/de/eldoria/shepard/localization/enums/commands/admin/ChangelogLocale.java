package de.eldoria.shepard.localization.enums.commands.admin;

public enum ChangelogLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.changelog.description"),
    /**
     * Localization key for subcommand addRole.
     */
    C_ADD_ROLE("command.changelog.subcommand.addRole"),
    /**
     * Localization key for subcommand removeRole.
     */
    C_REMOVE_ROLE("command.changelog.subcommand.removeRole"),
    /**
     * Localization key for subcommand activate.
     */
    C_ACTIVATE("command.changelog.subcommand.activate"),
    /**
     * Localization key for subcommand deactivate.
     */
    C_DEACTIVATE("command.changelog.subcommand.deactivate"),
    /**
     * Localization key for subcommand roles.
     */
    C_ROLES("command.changelog.subcommand.roles"),
    /**
     * Localization key for message logged roles.
     */
    M_LOGGED_ROLES("command.changelog.messages.loggedRoles"),
    /**
     * Localization key for message deactivated.
     */
    M_DEACTIVATED("command.changelog.messages.deactivated"),
    /**
     * Localization key for message activated.
     */
    M_ACTIVATED("command.changelog.messages.activated"),
    /**
     * Localization key for message added roles.
     */
    M_ADDED_ROLE("command.changelog.messages.addedRole"),
    /**
     * Localization key for message removed roles.
     */
    M_REMOVED_ROLE("command.changelog.messages.removedRole");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    ChangelogLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
