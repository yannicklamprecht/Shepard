package de.eldoria.shepard.localization.enums.commands;

public enum CommandLocale {
    BASE_COMMAND("command.command.baseCommand");


    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    CommandLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
