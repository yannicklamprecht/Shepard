package de.eldoria.shepard.localization.util;

import de.eldoria.shepard.localization.LanguageHandler;
import de.eldoria.shepard.wrapper.EventWrapper;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class TextLocalizer {
    private static final Pattern LOCALIZATION_CODE = Pattern.compile("\\$([a-zA-Z.]+?)\\$");
    private static final Pattern SIMPLE_LOCALIZATION_CODE = Pattern.compile("^([a-zA-Z]+?\\.[a-zA-Z.]+)$");

    /**
     * Get a localized message back.
     *
     * @param message String with localize codes coed must have format: $[a-zA-Z.]+?$
     * @param wrapper message wrapper for localization data
     * @return String where localize codes are replaced with guild language.
     * @deprecated This method is deprecated and should be replaced with {@link #localizeByWrapper(String, EventWrapper, Replacement...)} for more stable usage.
     */
    @Deprecated
    public static String localizeAll(String message, EventWrapper wrapper) {
        return localizeAllAndReplace(message, wrapper);
    }

    /**
     * Get a localized message back.
     *
     * @param message String with localize codes coed must have format: $[a-zA-Z.]+?$
     * @param channel message channel for localization data
     * @return String where localize codes are replaced with guild language.
     * @deprecated This method is deprecated and should be replaced with {@link #localizeByChannel(String, MessageChannel, Replacement...)} for more stable usage.
     */
    @Deprecated
    public static String localizeAllByChannel(String message, MessageChannel channel) {
        if (channel instanceof TextChannel) {
            TextChannel guildChannel = (TextChannel) channel;
            return localizeAndReplace(message, guildChannel.getGuild());
        } else {
            return localizeAndReplace(message, null);
        }
    }

    public static String localizeByWrapper(String message, EventWrapper wrapper, Replacement... replacements) {
        Guild guild = null;
        if (wrapper.isGuildEvent()) {
            guild = wrapper.getGuild().get();
        }
        return localizeByGuild(message, guild, replacements);
    }


    public static String localizeByChannel(String message, MessageChannel channel, Replacement... replacements) {
        if (channel instanceof TextChannel) {
            TextChannel guildChannel = (TextChannel) channel;
            return localizeByGuild(message, guildChannel.getGuild(), replacements);
        } else {
            return localizeByGuild(message, null, replacements);
        }
    }


    private static String localizeByGuild(String message, Guild guild, Replacement... replacements) {
        if (message == null) {
            return null;
        }
        LanguageHandler locale = LanguageHandler.getInstance();
        String result;
        // If the matcher doesn't find any key we assume its a simple message.
        if (!LOCALIZATION_CODE.matcher(message).find()) {
            if (!SIMPLE_LOCALIZATION_CODE.matcher(message).find()) {
                return message;
            }
            result = locale.getLanguageString(guild, message);
        } else {
            // find locale codes in message
            Matcher matcher = LOCALIZATION_CODE.matcher(message);
            List<String> keys = new ArrayList<>();
            while (matcher.find()) {
                keys.add(matcher.group(1));
            }

            result = message;

            for (String match : keys) {
                //Replace current placeholders with replacements
                String languageString = locale.getLanguageString(guild, match);
                result = result.replace("$" + match + "$", languageString);
            }
        }
        for (Replacement replacement : replacements) {
            result = replacement.invoke(result);
        }
        return result;
    }

    /**
     * Translates a String with Placeholders.
     * Can handle multiple messages with replacements. Add replacements in the right order.
     *
     * @param message      Message to translate
     * @param guild        message wrapper for localization data. Can be null.
     * @param replacements Replacements in the right order.
     * @return Replaced Messages
     * @deprecated This method is deprecated and should be replaced with {@link #localizeByGuild(String, Guild, Replacement...)} for more stable usage.
     */
    @Deprecated
    private static String localizeAndReplace(String message, @Nullable Guild guild, String... replacements) {
        if (message == null) {
            return null;
        }
        LanguageHandler locale = LanguageHandler.getInstance();

        // If the matcher doesn't find any key we assume its a simple message.
        if (!LOCALIZATION_CODE.matcher(message).find()) {
            if (SIMPLE_LOCALIZATION_CODE.matcher(message).find()) {
                String result = locale.getLanguageString(guild, message);
                result = applyNumericReplacements(result, replacements);
                return result;
            }
            return message;
        } else {
            // find locale codes in message
            Matcher matcher = LOCALIZATION_CODE.matcher(message);
            List<String> keys = new ArrayList<>();
            while (matcher.find()) {
                keys.add(matcher.group(1));
            }

            String result = message;

            for (String match : keys) {
                //Replace current placeholders with replacements
                String languageString = locale.getLanguageString(guild, match);
                result = result.replace("$" + match + "$", languageString);

                result = applyNumericReplacements(result, replacements);
            }
            return result;
        }
    }

    @Deprecated
    private static String applyNumericReplacements(String result, String[] replacements) {
        int replacementIndex = 0;

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
     * @deprecated This method is deprecated and should be replaced with {@link #localizeByWrapper(String, EventWrapper, Replacement...)} for more stable usage.
     */
    @Deprecated
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
     * @deprecated This method is deprecated and should be replaced with {@link #localizeByGuild(String, Guild, Replacement...)} for more stable usage.
     */
    @Deprecated
    public static String localizeAllAndReplace(String message, Guild guild, String... replacements) {
        return localizeAndReplace(message, guild, replacements);
    }
}
