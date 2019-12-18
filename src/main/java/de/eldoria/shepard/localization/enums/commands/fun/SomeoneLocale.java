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
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    SomeoneLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }


}
