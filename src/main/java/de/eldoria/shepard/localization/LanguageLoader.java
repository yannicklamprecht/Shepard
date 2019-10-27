package de.eldoria.shepard.localization;

import de.eldoria.shepard.localization.util.LanguageCode;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageLoader {
    private static final String BUNDLE_PATH = "de.eldoria.shepard.localization.bundles.";

    public static void initialize(LanguageHandler handler) {
        for (LanguageCode code : LanguageCode.values()) {
            String[] s = code.code.split("_");
            Locale locale = new Locale(s[0], s[1]);
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PATH, locale);
            handler.registerLanguageFile(code, bundle);
        }
    }
}
