package de.eldoria.shepard.localization.enums;

public enum InviteLocale {
    C_ADD_INVITE("command.invite.subcommand.addInvite");

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
