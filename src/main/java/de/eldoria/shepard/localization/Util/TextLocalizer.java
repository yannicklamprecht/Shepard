package de.eldoria.shepard.localization.Util;

import de.eldoria.shepard.localization.LanguageHandler;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextLocalizer {
    private static final Pattern LOCALIZATION_CODE = Pattern.compile("\\$([a-zA-Z.]+?)\\$");

    /**
     * Creates a localized field.
     *
     * @param title
     * @param description
     * @param inline
     * @param messageContext
     * @return
     */
    public static MessageEmbed.Field getLocalizedField(String title, String description, boolean inline,
                                                       MessageEventDataWrapper messageContext) {
        return new MessageEmbed.Field(fastLocale(title, messageContext),
                fastLocale(description, messageContext),
                inline);
    }

    /**
     * Get a localized message back.
     *
     * @param message        String with localize codes coed must have format: $[a-zA-Z.]+?$
     * @param messageContext messageContext
     * @return String where localize codes are replaced with guild language.
     */
    public static String fastLocale(String message, MessageEventDataWrapper messageContext) {
        return fastLocale(message, messageContext.getGuild());
    }

    /**
     * Get a localized message back.
     *
     * @param message String with localize codes coed must have format: $[a-zA-Z.]+?$
     * @param guild   guild for message localization
     * @return String where localize codes are replaced with guild language.
     */
    public static String fastLocale(String message, Guild guild) {
        LanguageHandler locale = LanguageHandler.getInstance();

        Matcher matcher = LOCALIZATION_CODE.matcher(message);
        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }

        String result = message;
        for (String match : matches) {
            String languageString = locale.getLanguageString(guild, match);
            result = result.replace(match, languageString);
        }
        return result;

    }

}
