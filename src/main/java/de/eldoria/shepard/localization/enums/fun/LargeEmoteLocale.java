package de.eldoria.shepard.localization.enums.fun;

public enum LargeEmoteLocale {
    DESCRIPTION("command.largeEmote.description"),
    A_EMOTE("command.largeEmote.argument.emote");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    LargeEmoteLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }
}
