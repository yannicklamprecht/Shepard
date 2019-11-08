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
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    PrivateMessageLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
