package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.database.types.HentaiImage;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                .prepareStatement("SELECT * from shepard_func.get_hentai_image()")) {
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
}
