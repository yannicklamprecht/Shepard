package de.eldoria.shepard.localization.enums.commands.util;

public enum RepoLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.repo.description"),

    M_TITLE("command.repo.message.title"),
    M_TAKE_A_LOOK("command.repo.message.takeALook");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    RepoLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
