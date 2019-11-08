package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.database.types.GuessGameImage;
import de.eldoria.shepard.database.types.Rank;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static de.eldoria.shepard.database.DbUtil.getScoreListFromResult;
import static de.eldoria.shepard.database.DbUtil.handleExceptionAndIgnore;

public final class GuessGameData {

    private GuessGameData() {
    }

    /**
     * Saves a image set to database.
     *
     * @param croppedImage   link of cropped image
     * @param fullImage      link of full image
     * @param hentai         true if its a hentai image
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean addHentaiImage(String croppedImage, String fullImage, boolean hentai,
                                         MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_guess_game_image(?,?,?)")) {
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

    /**
     * Get a random hentai image set from database.
     *
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return hentai image object
     */
    public static GuessGameImage getHentaiImage(MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_hentai_image_data()")) {
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new GuessGameImage(result.getString("cropped_image"),
                        result.getString("full_image"),
                        result.getBoolean("hentai"));
            }

        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
        }
        return null;
    }

    /**
     * Get a specific hentai image set from database.
     *
     * @param link           the full or cropped image link
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return hentai image object
     */
    public static GuessGameImage getHentaiImage(String link, MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_image_set(?)")) {
            statement.setString(1, link);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new GuessGameImage(result.getString("cropped_image"),
                        result.getString("full_image"),
                        result.getBoolean("hentai"));
            }

        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
        }
        return null;
    }

    /**
     * Removes a hentai image set from database.
     *
     * @param imageUrl       url of cropped ir full image.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
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

    /**
     * Changes the NSFW flag of a image.
     *
     * @param imageUrl       url of cropped ir full image.
     * @param nsfw           true if the image is nsfw
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean changeImageFlag(String imageUrl, boolean nsfw, MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.change_hentai_image_flag(?,?)")) {
            statement.setString(1, imageUrl);
            statement.setBoolean(2, nsfw);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Add the score to the score in the database. Negative score subtracts from score.
     *
     * @param guild          Guild where the score should be applied
     * @param users          List of users where the score should be applied
     * @param score          The score which should be applied
     * @param messageContext messageContext from command sending for error handling. Can be null.
     */
    public static void addVoteScore(Guild guild, List<User> users, int score,
                                    MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_guess_game_score(?,?,?)")) {
            statement.setString(1, guild.getId());
            Array ids = DatabaseConnector.getConn().createArrayOf("varchar",
                    users.stream().map(ISnowflake::getId).toArray());
            statement.setArray(2, ids);
            statement.setInt(3, score);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
        }
    }

    /**
     * Get the top x on a guild.
     *
     * @param guild          Guild where you want to have the top x
     * @param scoreAmount    Amount of entries. For the top 10 enter a 10.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return sorted list of ranks in descending order.
     */
    public static List<Rank> getTopScore(Guild guild, int scoreAmount, MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_guess_game_top_score(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setInt(2, scoreAmount);
            return getScoreListFromResult(statement.executeQuery());
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
        }
        return Collections.emptyList();
    }

    /**
     * Get the global top x. The score of all guilds is accumulated for each user.
     *
     * @param scoreAmount    Amount of entries. For the top 10 enter a 10.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return sorted list of ranks in descending order.
     */
    public static List<Rank> getGlobalTopScore(int scoreAmount, MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_guess_game_global_top_score(?)")) {
            statement.setInt(1, scoreAmount);
            return getScoreListFromResult(statement.executeQuery());
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
        }
        return Collections.emptyList();
    }

    /**
     * Get the score of a user on a guild.
     *
     * @param guild          Guild for lookup
     * @param user           User for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return score of user.
     */
    public static int getUserScore(Guild guild, User user, MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_guess_game_user_score(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
        }
        return -1;
    }

    /**
     * Get the sum of all scores of a user.
     *
     * @param user           User for lookup.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return global score of user
     */
    public static int getGlobalUserScore(User user, MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_guess_game_global_user_score(?)")) {
            statement.setString(1, user.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }

        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
        }
        return -1;
    }
}
