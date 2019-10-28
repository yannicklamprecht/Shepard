package de.eldoria.shepard.localization.enums.commands.fun;

public enum SomeoneLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.someone.description"),
    /**
     * Localization key for message no one online.
     */
    M_NO_ONLINE("command.someone.message.noOnline"),
    /**
     * Localization key for message someone.
     */
    M_SOMEONE("command.someone.message.someone");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    SomeoneLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }


}
