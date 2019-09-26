package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.database.types.HentaiImage;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static de.eldoria.shepard.database.DbUtil.handleExceptionAndIgnore;

public final class HentaiOrNotData {

    private HentaiOrNotData() {
    }

    public static boolean addHentaiImage(String croppedImage, String fullImage, boolean hentai,
                                         MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_hentai_image(?,?,?)")) {
            statement.setString(1, croppedImage);
            statement.setString(2, fullImage);
            statement.setBoolean(3, hentai);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    public static HentaiImage getHentaiImage(MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_hentai_image_data()")) {
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new HentaiImage(result.getString("cropped_image"),
                        result.getString("full_image"),
                        result.getBoolean("hentai"));
            }

        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
        }
        return null;
    }

    public static boolean removeHentaiImage(String imageUrl, MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_hentai_image(?)")) {
            statement.setString(1, imageUrl);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    public static boolean addVoteScore(Guild guild, List<User> users, int score,
                                       MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_vote_score(?,?,?)")) {
            statement.setString(1, guild.getId());
            Array ids = DatabaseConnector.getConn().createArrayOf("varchar",
                    users.stream().map(ISnowflake::getId).toArray());
            statement.setArray(2, ids);
            statement.setInt(3, score);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }
}
