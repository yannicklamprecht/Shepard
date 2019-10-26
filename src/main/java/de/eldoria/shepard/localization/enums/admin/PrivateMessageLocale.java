package de.eldoria.shepard.localization.enums.admin;

public enum PrivateMessageLocale {
    MESSAGE_DESCRIPTION("command.privateMessage.description"),
    ANSWER_DESCRIPTION("command.privateAnswer.description"),
    A_NAME("command.privateMessage.argument.name"),
    A_MESSAGE("command.privateMessage.argument.message");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    PrivateMessageLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }

}
