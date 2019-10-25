package de.eldoria.shepard.localization;

import de.eldoria.shepard.localization.Util.LanguageCode;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.ResourceBundle;

import static de.eldoria.shepard.database.queries.LanguageData.getGuildLanguage;

public class LanguageHandler {
    private static LanguageHandler instance;
    private HashMap<LanguageCode, ResourceBundle> languages = new HashMap<>();

    public static void initialize() {
        if (instance != null) {
            return;
        } else {
            instance = new LanguageHandler();
        }
        //TODO: Load languages
    }

    public ResourceBundle getLanguage(LanguageCode languageCode) {
        return languages.getOrDefault(languageCode, languages.get(LanguageCode.EN_US));
    }

    public String getLanguageString(Guild guild, String localeCode) {
        return getLanguage(getGuildLanguage(guild)).getString(localeCode);
    }

    /**
     * Replaced placeholder in a string with the x index of replacements.
     * @param localeCode locale code for localization
     * @param guild
     * @param replacements
     * @return
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

    public void registerLanguageFile(LanguageCode languageCode, ResourceBundle resourceBundle) {
        languages.put(languageCode, resourceBundle);
    }
}
