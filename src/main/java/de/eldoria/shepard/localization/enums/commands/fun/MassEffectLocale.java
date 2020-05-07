package de.eldoria.shepard.localization.enums.commands.fun;

public enum MassEffectLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.massEffect.description");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    MassEffectLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}