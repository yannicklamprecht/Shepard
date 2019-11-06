package de.eldoria.shepard.localization.enums.commands.fun;

public enum LargeEmoteLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.largeEmote.description"),
    /**
     * Localization key for argument emotes.
     */
    A_EMOTE("command.largeEmote.argument.emote");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    LargeEmoteLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
