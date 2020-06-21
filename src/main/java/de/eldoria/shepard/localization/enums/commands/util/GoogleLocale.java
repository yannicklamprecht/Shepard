package de.eldoria.shepard.localization.enums.commands.util;

public enum GoogleLocale {
    DESCRIPTION("command.google.description"),
    C_SEARCH("command.google.subCommand.search"),
    A_SEARCH("command.google.argument.search"),
    AD_SEARCH("command.google.argumentDescription.search"),
    M_MESSAGE("command.google.message"),
    M_GOOGLE("command.google.message.google"),
    M_ASK("command.google.message.ask"),
    M_YAHOO("command.google.message.yahoo"),
    M_BING("command.google.message.bing"),
    M_STARTPAGE("command.google.message.startpage"),
    M_AOL("command.google.message.aol"),
    M_DUCK("command.google.message.duck"),
    M_QUANT("command.google.message.quant"),
    M_WIKIPEDIA("command.google.message.wikipedia"),
    M_LMGTFY("command.google.message.lmgtfy"),
    M_MCSEU("command.google.message.mcseu"),
    M_REDDIT("command.google.message.reddit"),
    M_INVALID("command.google.invalid");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    GoogleLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
