package de.eldoria.shepard.localization.util;

import de.eldoria.shepard.localization.LanguageHandler;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;
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
     * @param wrapper message wrapper for localization data
     * @return String where localize codes are replaced with guild language.
     */
    public static String localizeAll(String message, EventWrapper wrapper) {
        return localizeAllAndReplace(message, wrapper);
    }

    /**
     * Get a localized message back.
     *
     * @param message String with localize codes coed must have format: $[a-zA-Z.]+?$
     * @param channel message channel for localization data
     * @return String where localize codes are replaced with guild language.
     */
    public static String localizeAllByChannel(String message, MessageChannel channel) {
        if (channel instanceof TextChannel) {
            TextChannel guildChannel = (TextChannel) channel;
            return localizeAndReplace(message, guildChannel.getGuild());
        } else {
            return localizeAndReplace(message, null);
        }
    }

    /**
     * Translates a String with Placeholders.
     * Can handle multiple messages with replacements. Add replacements in the right order.
     *
     * @param message      Message to translate
     * @param guild        message wrapper for localization data. Can be null.
     * @param replacements Replacements in the right order.
     * @return Replaced Messages
     */
    private static String localizeAndReplace(String message, @Nullable Guild guild, String... replacements) {
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

    /**
     * Translates a String with Placeholders.
     * Can handle multiple messages with replacements. Add replacements in the right order.
     *
     * @param message      Message to translate
     * @param wrapper      message wrapper for localization data
     * @param replacements Replacements in the right order.
     * @return Replaced Messages
     */
    public static String localizeAllAndReplace(String message, EventWrapper wrapper, String... replacements) {
        Guild guild = null;
        if (wrapper.isGuildEvent()) {
            guild = wrapper.getGuild().get();
        }
        return localizeAndReplace(message, guild, replacements);
    }

    /**
     * Translates a String with Placeholders.
     * Can handle multiple messages with replacements. Add replacements in the right order.
     *
     * @param message      Message to translate
     * @param guild        guild for localization data
     * @param replacements Replacements in the right order.
     * @return Replaced Messages
     */
    public static String localizeAllAndReplace(String message, Guild guild, String... replacements) {
        return localizeAndReplace(message, guild, replacements);
    }
}
