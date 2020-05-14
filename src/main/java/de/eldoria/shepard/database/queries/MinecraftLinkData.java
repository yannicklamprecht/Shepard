package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.database.types.MinecraftLink;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static de.eldoria.shepard.database.DbUtil.getIdRaw;
import static de.eldoria.shepard.database.DbUtil.handleException;

public final class MinecraftLinkData extends QueryObject {
    /**
     * create a new Minecraft Link data.
     *
     * @param source data source for information retrieval
     */
    public MinecraftLinkData(DataSource source) {
        super(source);
    }

    /**
     * Get a Minecraft link by user.
     *
     * @param user           User object.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return Minecraft Link object or null if no link was found
     */
    public MinecraftLink getLinkByUserId(User user, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_minecraft_link_user_id(?)")) {
            statement.setString(1, getIdRaw(user.getId()));
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new MinecraftLink(user, result.getString("uuid"));
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return null;
    }

    /**
     * Get a Minecraft link by uuid of a minecraft account.
     *
     * @param jda            jda instance
     * @param uuid           uuid of a minecraft account
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return Minecraft Link object or null if no link was found
     */
    public MinecraftLink getLinkByUUID(JDA jda, String uuid, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_minecraft_link_uuid(?)")) {
            statement.setString(1, getIdRaw(uuid));
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new MinecraftLink(jda, result.getString("user_id"), uuid.replace("-", ""));
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return null;
    }

    /**
     * Add a link code to database.
     *
     * @param code           Code to add
     * @param uuid           uuid of player
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean addLinkCode(String code, String uuid, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.add_minecraft_link_code(?,?)")) {
            statement.setString(1, code);
            statement.setString(2, uuid);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Get a uuid by code.
     *
     * @param code           code for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return UUID as String.
     */
    public String getUUIDByCode(String code, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.add_minecraft_link_code(?)")) {
            statement.setString(1, code);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getString(1);
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return null;
    }
}
