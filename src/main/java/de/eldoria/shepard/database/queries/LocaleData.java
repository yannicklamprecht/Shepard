package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.util.DefaultMap;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static de.eldoria.shepard.database.DbUtil.handleExceptionAndIgnore;

public class LocaleData {
    private static final Map<Long, String> languages = new DefaultMap<>(ShepardBot.getConfig().getPrefix());
    private static final Map<Long, Boolean> cacheDirty = new DefaultMap<>(true);

    private LocaleData() {
    }

    /**
     * Sets the locale_code for a guild.
     *
     * @param guild          Guild for which the locale_code should be set
     * @param locale_code         locale_code to set.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean setLanguage(Guild guild, String locale_code, MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_language(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, locale_code);
            statement.execute();
            languages.put(guild.getIdLong(), locale_code);
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }

        ShepardBot.getLogger().info("Changed locale_code of server " + guild.getName() + " to " + locale_code);
        return true;
    }

    /**
     * Get the prefix for a guild.
     *
     * @param guild          Guild object for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return Prefix as string
     */
    public static String getLanguage(Guild guild, MessageEventDataWrapper messageContext) {
        if (!languages.containsKey(guild.getIdLong()) || cacheDirty.get(guild.getIdLong())) {
            loadLanguage(guild);
        }

        return languages.get(guild.getIdLong());
    }

    private static void loadLanguage(Guild guild) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_language(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            languages.clear();
            if (result.next()) {
                String prefix = result.getString("locale_code");
                languages.put(guild.getIdLong(), prefix);
            }
            cacheDirty.put(guild.getIdLong(), false);
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, null);
        }
    }
}
