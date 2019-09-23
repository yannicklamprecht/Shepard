package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.util.DefaultMap;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static de.eldoria.shepard.database.DbUtil.handleExceptionAndIgnore;

public final class PrefixData {
    private static final Map<String, String> prefixes = new DefaultMap<>(ShepardBot.getConfig().getPrefix());
    private static boolean cacheDirty = true;

    private PrefixData() {
    }

    /**
     * Sets the prefix for a guild.
     *
     * @param guild  Guild for which the prefix should be set
     * @param prefix prefix to set.
     * @param messageContext  messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean setPrefix(Guild guild, String prefix, MessageEventDataWrapper messageContext) {
        cacheDirty = true;
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_prefix(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, prefix);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }

        ShepardBot.getLogger().info("Changed prefix of server " + guild.getName() + " to " + prefix);
        return true;
    }

    /**
     * Get the prefix for a guild.
     *
     * @param guild Guild object for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return Prefix as string
     */
    public static String getPrefix(Guild guild, MessageEventDataWrapper messageContext) {
        if (!cacheDirty) {
            return prefixes.get(guild.getId());
        }

        refreshPrefixes(messageContext);

        return getPrefix(guild, messageContext);
    }

    private static void refreshPrefixes(MessageEventDataWrapper messageContext) {
        if (!cacheDirty) {
            return;
        }
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_prefixes()")) {
            ResultSet result = statement.executeQuery();
            prefixes.clear();
            while (result.next()) {
                String guild = result.getString("guild_id");
                String prefix = result.getString("prefix");

                prefixes.put(guild, prefix);
            }

            cacheDirty = false;

        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
        }
    }
}
