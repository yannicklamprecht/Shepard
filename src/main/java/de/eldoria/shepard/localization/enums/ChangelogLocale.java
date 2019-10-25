package de.eldoria.shepard.localization.enums;

public enum ChangelogLocale {
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

    public final String localeCode;
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
