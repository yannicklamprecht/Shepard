package de.chojo.shepard.database.queries;

import de.chojo.shepard.ShepardBot;
import de.chojo.shepard.database.DatabaseConnector;
import de.chojo.shepard.util.DefaultMap;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static de.chojo.shepard.database.DbUtil.handleException;

public class Prefix {
    private static final DefaultMap<String, String> prefixes = new DefaultMap<>(ShepardBot.getConfig().getPrefix());
    private static boolean cacheDirty = true;

    private Prefix(){}

    public static void setPrefix(Guild guild, String prefix, MessageReceivedEvent event) {
        cacheDirty = true;
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.set_prefix(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, prefix);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }

        System.out.println("Changed prefix of server " + guild.getName() + " to " + prefix);
    }

    public static String getPrefix(Guild guild, MessageReceivedEvent event) {
        if(!cacheDirty){
            return prefixes.get(guild.getId());
        }

        getPrefixes(event);

        return getPrefix(guild,event);
    }

    public static DefaultMap<String, String> getPrefixes(MessageReceivedEvent event) {
        if(!cacheDirty){
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

            cacheDirty = false;
            return prefixes;

        } catch (SQLException e) {
            handleException(e,event);
        }
        return prefixes;
    }



}
