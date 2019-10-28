package de.eldoria.shepard.localization.enums.commands.util;

public enum HelpLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.help.description"),
    A_COMMAND("command.help.message.usage"),
    M_USAGE("command.help.message.usage"),
    M_LIST_COMMANDS("command.help.message.listCommand"),
    M_BOT_CONFIG("command.help.message.botConfig"),
    M_ADMIN("command.help.message.admin"),
    M_EXCLUSIVE("command.help.message.exclusive"),
    M_FUN("command.help.message.fun"),
    M_UTIL("command.help.message.util"),
    M_MAYBE_USEFUL("command.help.message.maybeUseful"),
    M_INVITE_ME("command.help.message.inviteMe"),
    M_SUPPORT_SERVER("command.help.message.supportServer"),
    M_COMMANDS("command.help.message.commands");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    HelpLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
