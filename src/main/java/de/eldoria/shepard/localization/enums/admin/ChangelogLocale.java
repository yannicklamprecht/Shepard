package de.eldoria.shepard.localization.enums.admin;

public enum ChangelogLocale {
    DESCRIPTION("command.changelog.description"),
    C_ADD_ROLE("command.changelog.subcommand.addRole"),
    C_REMOVE_ROLE("command.changelog.subcommand.removeRole"),
    C_ACTIVATE("command.changelog.subcommand.activate"),
    C_DEACTIVATE("command.changelog.subcommand.deactivate"),
    C_ROLES("command.changelog.subcommand.roles"),
    M_LOGGED_ROLES("command.changelog.messages.loggedRoles"),
    M_DEACTIVATED("command.changelog.messages.deactivated"),
    M_ACTIVATED("command.changelog.messages.activated"),
    M_ADDED_ROLE("command.changelog.messages.addedRole"),
    M_REMOVED_ROLE("command.changelog.messages.removedRole");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    ChangelogLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }
}
