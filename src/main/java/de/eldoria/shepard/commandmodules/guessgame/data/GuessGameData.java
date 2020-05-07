package de.eldoria.shepard.commandmodules.guessgame.data;

import de.eldoria.shepard.commandmodules.guessgame.util.GuessGameImage;
import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.database.types.Rank;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static de.eldoria.shepard.database.DbUtil.getScoreListFromResult;
import static de.eldoria.shepard.database.DbUtil.handleException;

public final class GuessGameData extends QueryObject {

    /**
     * Create a new guess game data object.
     *
     * @param source data source for connection retrieval
     */
    public GuessGameData(DataSource source) {
        super(source);
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
    public boolean addHentaiImage(String croppedImage, String fullImage, boolean hentai,
                                  MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.add_guess_game_image(?,?,?)")) {
            statement.setString(1, croppedImage);
            statement.setString(2, fullImage);
            statement.setBoolean(3, hentai);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
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
    public GuessGameImage getImage(MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_hentai_image_data()")) {
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new GuessGameImage(result.getString("cropped_image"),
                        result.getString("full_image"),
                        result.getBoolean("hentai"));
            }

        } catch (SQLException e) {
            handleException(e, messageContext);
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
    public GuessGameImage getImage(String link, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_image_set(?)")) {
            statement.setString(1, link);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new GuessGameImage(result.getString("cropped_image"),
                        result.getString("full_image"),
                        result.getBoolean("hentai"));
            }

        } catch (SQLException e) {
            handleException(e, messageContext);
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
    public boolean removeImage(String imageUrl, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.remove_hentai_image(?)")) {
            statement.setString(1, imageUrl);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
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
    public boolean changeImageFlag(String imageUrl, boolean nsfw, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.change_hentai_image_flag(?,?)")) {
            statement.setString(1, imageUrl);
            statement.setBoolean(2, nsfw);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
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
    public void addVoteScore(Guild guild, List<User> users, int score,
                             MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.add_guess_game_score(?,?,?)")) {
            statement.setString(1, guild.getId());
            Array ids = conn.createArrayOf("varchar",
                    users.stream().map(ISnowflake::getId).toArray());
            statement.setArray(2, ids);
            statement.setInt(3, score);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
    }

    /**
     * Get the top x on a guild.
     *
     * @param guild          Guild where you want to have the top x
     * @param scoreAmount    Amount of entries. For the top 10 enter a 10.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @param jda            jda instance
     * @return sorted list of ranks in descending order.
     */
    public List<Rank> getTopScore(Guild guild, int scoreAmount, MessageEventDataWrapper messageContext, JDA jda) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_guess_game_top_score(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setInt(2, scoreAmount);
            return getScoreListFromResult(jda, statement.executeQuery());
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return Collections.emptyList();
    }

    /**
     * Get the global top x. The score of all guilds is accumulated for each user.
     *
     * @param scoreAmount    Amount of entries. For the top 10 enter a 10.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @param jda            jda instance
     * @return sorted list of ranks in descending order.
     */
    public List<Rank> getGlobalTopScore(int scoreAmount, MessageEventDataWrapper messageContext, JDA jda) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_guess_game_global_top_score(?)")) {
            statement.setInt(1, scoreAmount);
            return getScoreListFromResult(jda, statement.executeQuery());
        } catch (SQLException e) {
            handleException(e, messageContext);
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
    public int getUserScore(Guild guild, User user, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_guess_game_user_score(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
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
    public int getGlobalUserScore(User user, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_guess_game_global_user_score(?)")) {
            statement.setString(1, user.getId());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }

        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return -1;
    }
}
