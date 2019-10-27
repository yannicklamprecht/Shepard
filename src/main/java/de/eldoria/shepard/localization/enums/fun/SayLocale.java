package de.eldoria.shepard.localization.enums.fun;

public enum SayLocale {
    DESCRIPTION("command.say.description"),
    A_SAY("command.say.argument.say");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    SayLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }

}
