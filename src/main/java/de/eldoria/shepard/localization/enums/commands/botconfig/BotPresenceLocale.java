package de.eldoria.shepard.localization.enums.commands.botconfig;

public enum BotPresenceLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.botPresence.description"),
    /**
     * Localization key for subcommand playing.
     */
    C_PLAYING("command.botPresence.subcommand.playing"),
    /**
     * Localization key for subcommand streaming.
     */
    C_STREAMING("command.botPresence.subcommand.streaming"),
    /**
     * Localization key for subcommand listening.
     */
    C_LISTENING("command.botPresence.subcommand.listening"),
    /**
     * Localization key for subcommand clear.
     */
    C_CLEAR("command.botPresence.subcommand.clear"),
    /**
     * Localization key for argument twitchUrl.
     */
    A_TWITCH_URL("command.botPresence.argument.twitchUrl"),
    /**
     * Localization key for message playing.
     */
    M_PLAYING("command.botPresence.message.playing"),
    /**
     * Localization key for message streaming.
     */
    M_STREAMING("command.botPresence.message.streaming"),
    /**
     * Localization key for message listening.
     */
    M_LISTENING("command.botPresence.message.listening"),
    /**
     * Localization key for message clear.
     */
    M_CLEAR("command.botPresence.message.clear");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    BotPresenceLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
