package de.eldoria.shepard.localization.util;

public enum LocaleCode {
    /**
     * English language code.
     */
    EN_US("en_US"),
    /**
     * German language code.
     */
    DE_DE("de_DE");

    /**
     * Get the locale code with pattern [a-z]{2}_[A-Z]{2}.
     */
    public final String code;

    LocaleCode(String code) {
        this.code = code;
    }

    public static LocaleCode parse(String codeString) {
        for (LocaleCode code : LocaleCode.values()) {
            if (code.code.equalsIgnoreCase(codeString)) {
                return code;
            }
        }
        return null;
    }
}
