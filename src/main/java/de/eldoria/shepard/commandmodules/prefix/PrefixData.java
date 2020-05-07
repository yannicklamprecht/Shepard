package de.eldoria.shepard.commandmodules.prefix;

import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static de.eldoria.shepard.database.DbUtil.handleException;

@Slf4j
public final class PrefixData extends QueryObject {
    private final Config config;

    /**
     * Create a new prefix data object.
     *
     * @param source data source for information retrieval
     * @param config config for default prefix
     */
    public PrefixData(DataSource source, Config config) {
        super(source);
        this.config = config;
    }

    /**
     * Sets the prefix for a guild.
     *
     * @param guild          Guild for which the prefix should be set
     * @param prefix         prefix to set.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setPrefix(Guild guild, String prefix, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.set_prefix(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, prefix);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
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
    public String getPrefix(Guild guild, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_prefix(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                String prefix = result.getString(1);
                return prefix != null ? prefix : config.getGeneralSettings().getPrefix();
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        return config.getGeneralSettings().getPrefix();
    }
}
