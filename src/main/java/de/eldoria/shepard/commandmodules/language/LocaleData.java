package de.eldoria.shepard.commandmodules.language;

import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.localization.util.LocaleCode;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static de.eldoria.shepard.database.DbUtil.handleException;

@Slf4j
public class LocaleData extends QueryObject {
    /**
     * Create a new locale data object.
     *
     * @param source data source for information retrieval
     */
    public LocaleData(DataSource source) {
        super(source);
    }

    /**
     * Sets the languageCode for a guild.
     *
     * @param guild          Guild for which the languageCode should be set
     * @param localeCode     languageCode to set.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean setLanguage(Guild guild, LocaleCode localeCode, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.set_language(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, localeCode.code);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }

        log.debug("Changed languageCode of server {} to {}", guild.getName(), localeCode);
        return true;
    }

    /**
     * Get the prefix for a guild.
     *
     * @param guild Guild object for lookup
     * @return Prefix as string
     */
    public LocaleCode getLanguage(Guild guild) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_language(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                LocaleCode localeCode = LocaleCode.parse(result.getString(1));
                return Objects.requireNonNullElse(localeCode, LocaleCode.EN_US);
            }
        } catch (SQLException e) {
            handleException(e, null);
        }
        return LocaleCode.EN_US;
    }
}
