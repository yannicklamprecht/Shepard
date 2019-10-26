package de.eldoria.shepard.localization.enums.botconfig;

public enum BotPresenceLocale {
    DESCRIPTION("command.botPresence.description"),
    C_PLAYING("command.botPresence.subcommand.playing"),
    C_STREAMING("command.botPresence.subcommand.streaming"),
    C_LISTENING("command.botPresence.subcommand.listening"),
    C_CLEAR("command.botPresence.subcommand.clear"),
    A_TWITCH_URL("command.botPresence.argument.twitchUrl"),
    M_PLAYING("command.botPresence.message.playing"),
    M_STREAMING("command.botPresence.message.streaming"),
    M_LISTENING("command.botPresence.message.listening"),
    M_CLEAR("command.botPresence.message.clear");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    BotPresenceLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }

    }
