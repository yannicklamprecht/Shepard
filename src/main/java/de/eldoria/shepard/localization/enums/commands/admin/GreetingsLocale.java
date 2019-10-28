package de.eldoria.shepard.localization.enums.commands.admin;

public enum GreetingsLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.greeting.description"),
    /**
     * Localization key for subcommand setChannel.
     */
    C_SET_CHANNEL("command.greeting.subcommand.setChannel"),
    /**
     * Localization key for subcommand removeChannel.
     */
    C_REMOVE_CHANNEL("command.greeting.subcommand.removeChannel"),
    /**
     * Localization key for subcommand setMessage.
     */
    C_SET_MESSAGE("command.greeting.subcommand.setMessage"),
    /**
     * Localization key for message set channel.
     */
    M_SET_CHANNEL("command.greeting.messages.setChannel"),
    /**
     * Localization key for message removed channel.
     */
    M_REMOVED_CHANNEL("command.greeting.messages.removedChannel"),
    /**
     * Localization key for message set message.
     */
    M_SET_MESSAGE("command.greeting.messages.setMessage");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    GreetingsLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
