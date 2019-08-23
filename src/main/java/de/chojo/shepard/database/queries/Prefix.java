package de.chojo.shepard.database.queries;

import de.chojo.shepard.ShepardBot;
import de.chojo.shepard.database.DatabaseConnector;
import de.chojo.shepard.util.DefaultMap;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static de.chojo.shepard.database.DbUtil.getIdRaw;
import static de.chojo.shepard.database.DbUtil.handleException;

public class Prefix {
    private static DefaultMap<String, String> prefixes = new DefaultMap<>(ShepardBot.getConfig().getPrefix());
    private static boolean cache_dirty = true;

    public static void setPrefix(String guildId, String prefix, MessageReceivedEvent event) {
        cache_dirty = true;
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_prefix(?,?)")) {
            statement.setString(1, getIdRaw(guildId));
            statement.setString(2, prefix);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }

        System.out.println("Changed prefix of server " + guildId + " to " + prefix);
    }

    public static String getPrefix(String guildId, MessageReceivedEvent event) {
        if(!cache_dirty){
            return prefixes.get(guildId);
        }

        getPrefixes(event);

        return getPrefix(guildId,event);
    }

    public static DefaultMap<String, String> getPrefixes(MessageReceivedEvent event) {
        if(!cache_dirty){
            return prefixes;
        }
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT * from shepard_func.get_prefixes()")) {
            ResultSet result = statement.executeQuery();
            prefixes.clear();
            while (result.next()) {
                String guild = result.getString("guild_id");
                String prefix = result.getString("prefix");

                prefixes.put(guild, prefix);
            }

            cache_dirty = false;
            return prefixes;

        } catch (SQLException e) {
            handleException(e,event);
        }
        return prefixes;
    }



}
