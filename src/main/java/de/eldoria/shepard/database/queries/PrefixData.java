package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.util.DefaultMap;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static de.eldoria.shepard.database.DbUtil.handleException;

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
     * @param event  event from command sending for error handling. Can be null.
     */
    public static void setPrefix(Guild guild, String prefix, MessageReceivedEvent event) {
        cacheDirty = true;
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.set_prefix(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, prefix);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }

        System.out.println("Changed prefix of server " + guild.getName() + " to " + prefix);
    }

    /**
     * Get the prefix for a guild.
     *
     * @param guild Guild object for lookup
     * @param event event from command sending for error handling. Can be null.
     * @return Prefix as string
     */
    public static String getPrefix(Guild guild, MessageReceivedEvent event) {
        if (!cacheDirty) {
            return prefixes.get(guild.getId());
        }

        refreshPrefixes(event);

        return getPrefix(guild, event);
    }

    private static void refreshPrefixes(MessageReceivedEvent event) {
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
            handleException(e, event);
        }
    }


}
