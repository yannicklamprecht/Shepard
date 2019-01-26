package de.chojo.shepard;

import de.chojo.shepard.database.DatabaseQuery;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A class containing all settings for different discord servers.
 */
public final class Settings {
    private static final String DEFAULT_PREFIX = "&";
    private static Map<Guild, Properties> settings = new HashMap<>();

    /**
     * Get the prefix used for commands in a specific guild.
     *
     * @param context the guild to look for.
     * @return the prefix used for commands in the given guild.
     */
    public static String getPrefix(Guild context) {
        return getOrDefault(context, "prefix", DEFAULT_PREFIX);
    }

    /**
     * Change the prefix used for commands in a specific guild.
     *
     * @param context the guild to apply the changes to.
     * @param prefix the prefix for commands in the given guild.
     */
    public static void setPrefix(Guild context, char prefix) {
        set(context, "prefix", String.valueOf(prefix));

    }

    /**
     * Get a value for a guild by its key or a default value if not present.
     *
     * @param context the guild to look for.
     * @param key the key to look for for the given guild.
     * @param def the default value as a fallback if nothing is set yet.
     * @return the value if present, otherwise the default.
     */
    private static String getOrDefault(Guild context, String key, String def) {
        if (!settings.containsKey(context)) {
            settings.put(context, loadProperties(context));
        }
        return settings.get(context).getProperty(key, def);
    }

    /**
     * Set a key to a specific value for a specific guild.
     *
     * @param context the guild to apply the changes to.
     * @param key the key to apply the changes to for the given guild.
     * @param property the value to set.
     */
    private static void set(Guild context, String key, String property) {
        if (!settings.containsKey(context)) {
            settings.put(context, loadProperties(context));
        }
        DatabaseQuery.saveProperty(context, key, property);
        settings.get(context).setProperty(key, property);
    }

    /**
     * Load the properties from the database for a specific guild.
     *
     * @param context the guild to load properties for.
     * @return the loaded properties.
     */
    private static Properties loadProperties(Guild context) {
        return DatabaseQuery.loadProperties(context);
    }
}
