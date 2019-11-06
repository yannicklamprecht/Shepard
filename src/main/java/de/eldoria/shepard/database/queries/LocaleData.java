package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.localization.util.LocaleCode;
import de.eldoria.shepard.util.DefaultMap;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.eldoria.shepard.database.DbUtil.handleExceptionAndIgnore;

public final class LocaleData {
    private static final Map<Long, LocaleCode> languages = new HashMap<>();
    private static final DefaultMap<Long, Boolean> cacheDirty = new DefaultMap<>(true);

    private LocaleData() {
    }

    /**
     * Sets the languageCode for a guild.
     *
     * @param guild          Guild for which the languageCode should be set
     * @param localeCode     languageCode to set.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean setLanguage(Guild guild, LocaleCode localeCode, MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_language(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, localeCode.code);
            statement.execute();
            languages.put(guild.getIdLong(), localeCode);
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }

        ShepardBot.getLogger().info("Changed languageCode of server " + guild.getName() + " to " + localeCode);
        return true;
    }

    /**
     * Get the prefix for a guild.
     *
     * @param guild          Guild object for lookup
     * @return Prefix as string
     */
    public static LocaleCode getLanguage(Guild guild) {
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
            if (result.next()) {
                LocaleCode localeCode = LocaleCode.parse(result.getString(1));
                languages.put(guild.getIdLong(), Objects.requireNonNullElse(localeCode, LocaleCode.EN_US));
            }
            cacheDirty.put(guild.getIdLong(), false);
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, null);
        }
    }
}
