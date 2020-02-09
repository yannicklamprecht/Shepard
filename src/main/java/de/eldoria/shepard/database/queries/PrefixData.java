package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.util.DefaultMap;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static de.eldoria.shepard.database.DbUtil.handleExceptionAndIgnore;

@Slf4j
public final class PrefixData {
    private static final Map<Long, String> prefixes = new HashMap<>();
    private static final DefaultMap<Long, Boolean> cacheDirty = new DefaultMap<>(true);

    private PrefixData() {
    }

    /**
     * Sets the prefix for a guild.
     *
     * @param guild          Guild for which the prefix should be set
     * @param prefix         prefix to set.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean setPrefix(Guild guild, String prefix, MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_prefix(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, prefix);
            statement.execute();
            prefixes.put(guild.getIdLong(), prefix);
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
	
		log.debug("Changed prefix of server {} to {}", guild.getName(), prefix);
        return true;
    }

    /**
     * Get the prefix for a guild.
     *
     * @param guild          Guild object for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return Prefix as string
     */
    public static String getPrefix(Guild guild, MessageEventDataWrapper messageContext) {
        if (!prefixes.containsKey(guild.getIdLong()) || cacheDirty.get(guild.getIdLong())) {
            loadPrefix(guild);
        }
        return prefixes.get(guild.getIdLong());
    }

    private static void loadPrefix(Guild guild) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_prefix(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                String prefix = result.getString(1);
                if (prefix != null) {
                    prefixes.put(guild.getIdLong(), prefix);
                } else {
                    prefixes.put(guild.getIdLong(), ShepardBot.getConfig().getPrefix());
                }
            }
            cacheDirty.put(guild.getIdLong(), false);

        } catch (SQLException e) {
            handleExceptionAndIgnore(e, null);
        }
    }
}
