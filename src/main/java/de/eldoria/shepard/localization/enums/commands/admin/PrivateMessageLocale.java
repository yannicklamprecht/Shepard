package de.eldoria.shepard.localization.enums.commands.admin;

public enum PrivateMessageLocale {
    /**
     * Localization key for description of message command.
     */
    MESSAGE_DESCRIPTION("command.privateMessage.description"),
    /**
     * Localization key for description of reply command.
     */
    ANSWER_DESCRIPTION("command.privateAnswer.description"),
    /**
     * Localization key for argument name.
     */
    A_NAME("command.privateMessage.argument.name"),
    /**
     * Localization key for message message.
     */
    A_MESSAGE("command.privateMessage.argument.message");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    PrivateMessageLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
