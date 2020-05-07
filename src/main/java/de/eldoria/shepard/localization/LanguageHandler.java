package de.eldoria.shepard.localization;

import de.eldoria.shepard.commandmodules.language.LocaleData;
import de.eldoria.shepard.localization.util.LocaleCode;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;


@Slf4j
public class LanguageHandler implements ReqDataSource, ReqInit {
    private static final String BUNDLE_PATH = "locale";
    private static LanguageHandler instance;
    private final HashMap<LocaleCode, ResourceBundle> languages = new HashMap<>();
    private DataSource source;
    private LocaleData localeData;

    /**
     * Get the current language handler instance.
     *
     * @return language handler instance
     */
    public static LanguageHandler getInstance() {
        return instance;
    }

    private ResourceBundle getLanguageResource(LocaleCode localeCode) {
        return languages.getOrDefault(localeCode, languages.get(LocaleCode.EN_US));
    }

    /**
     * Get the language string of the locale code.
     *
     * @param guild     guild for language lookup
     * @param localetag locale code
     * @return message in the local code or the default language if key is missing.
     */
    public String getLanguageString(Guild guild, String localetag) {
        LocaleCode language;
        if (guild == null) {
            language = LocaleCode.EN_US;
        } else {
            language = localeData.getLanguage(guild);
        }
        if (getLanguageResource(language).containsKey(localetag)) {
            return getLanguageResource(language).getString(localetag);
        } else {
            log.warn("Missing localization for key: {} in language pack: {}. Using Fallback Language en_US",
                    localetag, language.code);
            ResourceBundle bundle = getLanguageResource(LocaleCode.EN_US);

            if (!bundle.containsKey(localetag)) {
                log.warn("Missing localisation for key {} in fallback language. Is this intended?", localetag);
            }

            return bundle.containsKey(localetag) ? bundle.getString(localetag) : localetag;
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

        log.debug("Loaded {} languages!", languages.size());

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
            log.warn("Found missing keys in language packs\n{}", String.join("\n", missingKeys));
        }
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    @Override
    public void init() {
        localeData = new LocaleData(source);

        if (instance != null) {
            throw new RuntimeException("Tried to create a new language handler.");
        } else {
            instance = this;
        }
        instance.loadLanguages();
    }
}
