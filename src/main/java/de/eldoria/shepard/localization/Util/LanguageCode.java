package de.eldoria.shepard.localization.Util;

public enum LanguageCode {
    EN_US("en_US"),
    DE_DE("de_DE");

    /**
     * Get the locale code with pattern [a-z]{2}_[A-Z]{2}.
     */
    public final String code;

    LanguageCode(String code) {
        this.code = code;
    }
}
