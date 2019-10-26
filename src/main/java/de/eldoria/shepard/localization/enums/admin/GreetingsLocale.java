package de.eldoria.shepard.localization.enums.admin;

public enum GreetingsLocale {
    DESCRIPTION("command.greeting.description"),
    C_SET_CHANNEL("command.greeting.subcommand.setChannel"),
    C_REMOVE_CHANNEL("command.greeting.subcommand.removeChannel"),
    C_SET_MESSAGE("command.greeting.subcommand.setMessage"),
    M_SET_CHANNEL("command.greeting.messages.setChannel"),
    M_REMOVED_CHANNEL("command.greeting.messages.removedChannel"),
    M_SET_MESSAGE("command.greeting.messages.setMessage");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    GreetingsLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }
}
