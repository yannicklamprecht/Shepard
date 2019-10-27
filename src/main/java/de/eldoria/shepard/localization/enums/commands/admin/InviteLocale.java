package de.eldoria.shepard.localization.enums.admin;

public enum InviteLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.invite.description"),
    /**
     * Localization key for subcommand addInvite.
     */
    C_ADD_INVITE("command.invite.subcommand.addInvite"),
    /**
     * Localization key for subcommand removeInvite.
     */
    C_REMOVE_INVITE("command.invite.subcommand.removeInvite"),
    /**
     * Localization key for subcommand refreshInvite.
     */
    C_REFRESH_INVITES("command.invite.subcommand.refreshInvites"),
    /**
     * Localization key for subcommand showInvites.
     */
    C_SHOW_INVITES("command.invite.subcommand.showInvites"),
    /**
     * Localization key for argument invite code or url.
     */
    A_CODE("command.invite.argument.codeOfInvite"),
    /**
     * Localization key for argument invite name.
     */
    A_INVITE_NAME("command.invite.argument.inviteName"),
    /**
     * Localization key for message registered invites.
     */
    M_REGISTERED_INVITES("command.invite.messages.registeredInvites"),
    /**
     * Localization key for message code.
     */
    M_CODE("command.invite.messages.code"),
    /**
     * Localization key for message usage count.
     */
    M_USAGE_COUNT("command.invite.messages.usageCount"),
    /**
     * Localization key for message invite name.
     */
    M_INVITE_NAME("command.invite.messages.inviteName"),
    /**
     * Localization key for message removed non existent invites.
     */
    M_REMOVED_NON_EXISTENT_INVITES("command.invite.messages.removedNonExistentInvites"),
    /**
     * Localization key for message removed invite.
     */
    M_REMOVED_INVITE("command.invite.messages.removedInvite"),
    /**
     * Localization key for message added invite.
     */
    M_ADDED_INVITE("command.invite.messages.addedInvite");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    InviteLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }

}
