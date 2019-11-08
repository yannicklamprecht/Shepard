package de.eldoria.shepard.localization.util;

public enum LocaleCode {
    /**
     * English language code.
     */
    EN_US("en_US", "English"),
    /**
     * German language code.
     */
    DE_DE("de_DE", "Deutsch");

    /**
     * Get the locale code with pattern [a-z]{2}_[A-Z]{2}.
     */
    public final String code;
    /**
     * Get the full language name.
     */
    public final String languageName;

    /**
     * Create a new locale code.
     * @param code language code
     * @param languageName name of the language
     */
    LocaleCode(String code, String languageName) {
        this.code = code;
        this.languageName = languageName;
    }

    /**
     * Parse a string to a language code.
     * @param codeString string to parse
     * @return the locale code or null if parse failed
     */
    public static LocaleCode parse(String codeString) {
        for (LocaleCode code : LocaleCode.values()) {
            if (code.code.equalsIgnoreCase(codeString)) {
                return code;
            }
            if (code.languageName.equalsIgnoreCase(codeString)) {
                return code;
            }
        }
        return null;
    }
}
