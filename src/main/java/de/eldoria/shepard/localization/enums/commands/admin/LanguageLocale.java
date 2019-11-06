package de.eldoria.shepard.localization.enums.commands.admin;

public enum LanguageLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.language.description"),
    /**
     * Localization key for subcommand set.
     */
    C_SET("command.language.subcommand.set"),
    /**
     * Localization key for subcommand reset.
     */
    C_RESET("command.language.subcommand.reset"),
    /**
     * Localization key for subcommand reset.
     */
    C_LIST("command.language.subcommand.list"),
    /**
     * Localization key for argument language format.
     */
    A_LANGUAGE_CODE_FORMAT("command.language.subcommand.languageCodeFormat"),
    /**
     * Localization key for message changed.
     */
    M_CHANGED("command.language.message.changed"),
    /**
     * Localization key for message list.
     */
    M_LIST("command.language.message.list"),
    /**
     * Localization key for message submit.
     */
    M_SUBMIT("command.language.message.submit");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    LanguageLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }


}
