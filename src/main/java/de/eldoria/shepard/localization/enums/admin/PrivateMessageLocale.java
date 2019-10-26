package de.eldoria.shepard.localization.enums.admin;

public enum PrivateAnswerLocale {
    A_NAME("command.privateAnswer.argument.name"),
    A_MESSAGE("command.privateAnswer.argument.message"),
    M_INVALID_CHANNEL("command.privateAnswer.message.invalidChannel");

    public final String localeCode;
    public final String replacement;

    PrivateAnswerLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }

}
