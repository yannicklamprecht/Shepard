package de.eldoria.shepard.localization;

import de.eldoria.shepard.database.queries.LocaleData;
import de.eldoria.shepard.localization.util.LocaleCode;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageHandler {
    private static final String BUNDLE_PATH = "locale";
    private static LanguageHandler instance;
    private HashMap<LocaleCode, ResourceBundle> languages = new HashMap<>();

    private static void initialize() {
        if (instance != null) {
            return;
        } else {
            instance = new LanguageHandler();
        }
        instance.loadLanguages();
    }

    private ResourceBundle getLanguageResource(LocaleCode localeCode) {
        return languages.getOrDefault(localeCode, languages.get(LocaleCode.EN_US));
    }

    public String getLanguageString(Guild guild, String localeCode) {
        return getLanguageResource(LocaleData.getLanguage(guild, null)).getString(localeCode);
    }

    /**
     * Replaced placeholder in a string with the x index of replacements.
     *
     * @param localeCode   locale code for localization
     * @param guild        guild for language lookup.
     * @param replacements array of replacements for message placeholder.
     * @return localized message with replace placeholder.
     */
    public String getReplacedString(String localeCode, Guild guild, String... replacements) {
        String languageString = getLanguageString(guild, localeCode);
        for (int i = 0; i < replacements.length; i++) {
            languageString = languageString.replace("%" + i + "%", replacements[i]);
        }
        return languageString;
    }

    public static LanguageHandler getInstance() {
        initialize();
        return instance;
    }

    private void loadLanguages() {
        for (LocaleCode code : LocaleCode.values()) {
            String[] s = code.code.split("_");
            Locale locale = new Locale(s[0], s[1]);
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PATH, locale);
            languages.put(code, bundle);
        }
    }

}
