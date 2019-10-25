package de.eldoria.shepard.localization.enums;

public enum InviteLocale {
    C_ADD_INVITE("command.invite.subcommand.addInvite"),
    C_REMOVE_INVITE("command.invite.subcommand.removeInvite"),
    C_REFRESH_INVITES("command.invite.subcommand.refreshInvites"),
    C_SHOW_INVITES("command.invite.subcommand.showInvites"),
    C_CODE("command.invite.subcommand.codeOfInvite"),
    C_INVITE_NAME("command.invite.subcommand.inviteName"),
    M_REGISTERED_INVITES("command.invite.messages.registeredInvites"),
    M_CODE("command.invite.messages.code"),
    M_USAGE_COUNT("command.invite.messages.usageCount"),
    M_INVITE_NAME("command.invite.messages.inviteName"),
    M_REMOVED_NON_EXISTENT_INVITES("command.invite.messages.removedNonExistentInvites"),
    M_REMOVED_INVITE("command.invite.messages.removedInvite"),
    M_ADDED_INVITE("command.invite.messages.addedInvite");

    public final String localeCode;
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
