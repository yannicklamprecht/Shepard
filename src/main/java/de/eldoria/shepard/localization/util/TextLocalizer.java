package de.eldoria.shepard.localization.util;

import de.eldoria.shepard.localization.LanguageHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextLocalizer {
    private static final Pattern LOCALIZATION_CODE = Pattern.compile("\\$([a-zA-Z.]+?)\\$");

    /**
     * Get a localized message back.
     *
     * @param message String with localize codes coed must have format: $[a-zA-Z.]+?$
     * @param channel messageContext
     * @return String where localize codes are replaced with guild language.
     */
    public static String localizeAll(String message, TextChannel channel) {
        return localizeAll(message, channel.getGuild());
    }

    /**
     * Get a localized message back.
     *
     * @param message String with localize codes coed must have format: $[a-zA-Z.]+?$
     * @param guild   guild for message localization
     * @return String where localize codes are replaced with guild language.
     */
    public static String localizeAll(String message, Guild guild) {
        return localizeAllAndReplace(message, guild);
    }

    /**
     * Translates a String with Placeholders.
     * Can handle multiple messages with replacements. Add replacements in the right order.
     *
     * @param message      Message to translate
     * @param guild        guild for locale detection
     * @param replacements Replacements in the right order.
     * @return Replaced Messages
     */
    public static String localizeAllAndReplace(String message, Guild guild, String... replacements) {
        if (message == null) {
            return null;
        }
        LanguageHandler locale = LanguageHandler.getInstance();

        Matcher matcher = LOCALIZATION_CODE.matcher(message);
        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }

        String result = message;
        int replacementIndex = 0;
        for (String match : matches) {
            //Replace current placeholders with replacements
            String languageString = locale.getLanguageString(guild, match);
            result = result.replace("$" + match + "$", languageString);

            //Search for replacement
            for (int i = 0; i < replacements.length; i++) {
                //Search till no replacement is found!
                if (result.contains("%" + i + "%")) {
                    result = result.replace("%" + i + "%", replacements[replacementIndex]);
                    replacementIndex++;
                } else {
                    break;
                }
            }
        }
        return result;
    }
}
