package de.eldoria.shepard.localization;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.database.queries.LocaleData;
import de.eldoria.shepard.localization.util.LocaleCode;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

public class LanguageHandler {
    private static final String BUNDLE_PATH = "locale";
    private static LanguageHandler instance;
    private final HashMap<LocaleCode, ResourceBundle> languages = new HashMap<>();

    private static void initialize() {
        if (instance != null) {
            return;
        } else {
            instance = new LanguageHandler();
        }
        instance.loadLanguages();
    }

    /**
     * Get the current language handler instance.
     *
     * @return language handler instance
     */
    public static LanguageHandler getInstance() {
        initialize();
        return instance;
    }

    private ResourceBundle getLanguageResource(LocaleCode localeCode) {
        return languages.getOrDefault(localeCode, languages.get(LocaleCode.EN_US));
    }

    /**
     * Get the language string of the locale code.
     *
     * @param guild      guild for language lookup
     * @param localeCode locale code
     * @return message in the local code or the default language if key is missing.
     */
    public String getLanguageString(Guild guild, String localeCode) {
        LocaleCode language = LocaleData.getLanguage(guild);
        if (getLanguageResource(language).containsKey(localeCode)) {
            return getLanguageResource(language).getString(localeCode);
        } else {
            ShepardBot.getLogger().error("Missing localization for key: " + localeCode + " in language pack: "
                    + language.code + ". Using Fallback Language en_US");
            MessageSender.sendSimpleErrorEmbed("Missing localization for key: " + localeCode + " in language pack: "
                    + language.code + ". Using Fallback Language en_US", Normandy.getErrorChannel());
            ResourceBundle bundle = getLanguageResource(LocaleCode.EN_US);

            if (!bundle.containsKey(localeCode)) {
                ShepardBot.getLogger().error("Missing localisation for key " + localeCode + " in fallback language."
                + " Is this intended?");
            }

            return bundle.containsKey(localeCode) ? bundle.getString(localeCode) : localeCode;
        }
    }

    /**
     * Replaced placeholder in a string with the x index of replacements.
     *
     * @param localeCode   locale code for localization
     * @param guild        guild for language lookup.
     * @param replacements array of replacements for message placeholder.
     * @return localized message with replace placeholder.
     */
    @Deprecated
    public String getReplacedString(String localeCode, Guild guild, String... replacements) {
        String languageString = getLanguageString(guild, localeCode);
        for (int i = 0; i < replacements.length; i++) {
            languageString = languageString.replace("%" + i + "%", replacements[i]);
        }
        return languageString;
    }

    private void loadLanguages() {
        for (LocaleCode code : LocaleCode.values()) {
            String[] s = code.code.split("_");
            Locale locale = new Locale(s[0], s[1]);
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PATH, locale);
            languages.put(code, bundle);
        }

        ShepardBot.getLogger().info("Loaded " + languages.size() + " languages!");

        Set<String> keySet = new HashSet<>();
        for (ResourceBundle resourceBundle : languages.values()) {
            keySet.addAll(resourceBundle.keySet());
        }

        List<String> missingKeys = new ArrayList<>();
        for (ResourceBundle resourceBundle : languages.values()) {
            for (String key : keySet) {
                if (!resourceBundle.containsKey(key)) {
                    missingKeys.add(key + "@" + resourceBundle.getLocale());
                }
            }
        }

        if (!missingKeys.isEmpty()) {
            MessageSender.sendSimpleErrorEmbed("Found missing keys in language packs"
                            + System.lineSeparator() + String.join(System.lineSeparator(), missingKeys),
                    Normandy.getErrorChannel());
        }
    }
}
