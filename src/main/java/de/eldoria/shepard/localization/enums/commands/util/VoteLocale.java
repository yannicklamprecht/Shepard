package de.eldoria.shepard.localization.enums.commands.util;

public enum VoteLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.vote.description"),
    /**
     * Localization key for description.
     */
    M_TITLE("command.vote.message.title"),
    /**
     * Localization key for description.
     */
    M_TEXT("command.vote.message.text"),
    /**
     * Localization key for description.
     */
    M_CLICK("command.vote.message.click");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    VoteLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }


}
