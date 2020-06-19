package de.eldoria.shepard.commandmodules.ban.data;

import de.eldoria.shepard.commandmodules.ban.types.BanDataType;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Member;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BanData extends QueryObject {
    /**
     * Default constructor for data object.
     *
     * @param source data source for information retrieval
     */
    public BanData(DataSource source) {
        super(source);
    }

    public boolean addBan(Member user, String intervall, EventWrapper wrapper) {
        try(var conn = source.getConnection(); PreparedStatement stmt =
                conn.prepareStatement("SELECT shepard_func.shepard_func.add_temp_ban(?, ?, ?)")){
            stmt.setLong(1, wrapper.getGuild().get().getIdLong());
            stmt.setLong(2, user.getIdLong());
            stmt.setString(3, intervall);
            return stmt.execute();
        }
        catch (SQLException e){
            DbUtil.handleException(e, wrapper);
        }
        return false;
    }

    public List<BanDataType> getExpiredBans(EventWrapper wrapper) {
        try(var conn = source.getConnection(); PreparedStatement stmt =
                conn.prepareStatement("SELECT * FROM shepard_func.get_temp_ban()")) {
            List<BanDataType> result = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                result.add(new BanDataType(
                        rs.getString("guild_id"),
                        rs.getString("user_id")
                ));
            }
            return result;

        }
        catch (SQLException e){
            DbUtil.handleException(e, wrapper);
        }
        return null;
    }
}
