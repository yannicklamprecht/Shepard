package de.chojo.shepard;

import de.chojo.shepard.database.DatabaseQuery;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class Settings {
    private static final String DEFAULT_PREFIX = "&";
    private static Map<Guild, Properties> settings = new HashMap<>();

    public static String getPrefix(Guild context) {
        return getOrDefault(context, "prefix", DEFAULT_PREFIX);
    }

    public static void setPrefix(Guild context, char prefix) {
        set(context, "prefix", String.valueOf(prefix));

    }

    private static String getOrDefault(Guild context, String key, String def) {
        if (!settings.containsKey(context)) {
            settings.put(context, loadProperties(context));
        }
        return settings.get(context).getProperty(key, def);
    }

    private static void set(Guild context, String key, String property) {
        if (!settings.containsKey(context)) {
            settings.put(context, loadProperties(context));
        }
        DatabaseQuery.saveProperty(context, key, property);
        settings.get(context).setProperty(key, property);
    }

    private static Properties loadProperties(Guild context) {
        return DatabaseQuery.loadProperties(context);
    }
}
