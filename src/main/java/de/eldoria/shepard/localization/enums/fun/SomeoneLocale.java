package de.eldoria.shepard.localization.enums.fun;

public enum SomeoneLocale {
    DESCRIPTION("command.someone.description"),
    M_NO_ONLINE("command.someone.message.noOnline"),
    M_SOMEONE("command.someone.message.someone");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    SomeoneLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }


}
