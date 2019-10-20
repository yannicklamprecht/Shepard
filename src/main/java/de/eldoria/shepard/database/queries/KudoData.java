package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.database.types.Rank;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static de.eldoria.shepard.database.DbUtil.getScoreListFromResult;
import static de.eldoria.shepard.database.DbUtil.handleExceptionAndIgnore;

public final class KudoData {
    private KudoData() {
    }

    /**
     * Try to take the points from the user.
     *
     * @param guild          guild where the points should be taken.
     * @param user           user from who the points should be taken.
     * @param points         points to take
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the points where taken.
     */
    public static boolean tryTakePoints(Guild guild, User user, int points, MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.try_take_rubber_points(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            statement.setInt(3, points);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getBoolean(1);
            }
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
        }
        return false;
    }

    /**
     * Add the score to the score in the database. Negative score subtracts from score.
     *
     * @param guild          Guild where the score should be applied
     * @param user           user where the score should be applied
     * @param score          The score which should be applied
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean addRubberPoints(Guild guild, User user, int score,
                                          MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_rubber_points(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            statement.setInt(3, score);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Add the amount to the amount in the database. Negative amount subtracts from amount.
     *
     * @param guild          Guild where the amount should be applied
     * @param user           user where the amount should be applied
     * @param amount         The amount which should be applied
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public static boolean addFreeRubberPoints(Guild guild, User user, int amount,
                                              MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_free_rubber_points(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            statement.setInt(3, amount);
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, messageContext);
            return false;
        }
        return true;
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
                .prepareStatement("SELECT * from shepard_func.get_rubber_points_top_score(?,?)")) {
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
                .prepareStatement("SELECT * from shepard_func.get_rubber_points_global_top_score(?)")) {
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
                .prepareStatement("SELECT * from shepard_func.get_rubber_points_user_score(?,?)")) {
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
                .prepareStatement("SELECT * from shepard_func.get_rubber_points_global_user_score(?)")) {
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

    /**
     * Get the sum of all scores of a user.
     *
     * @param guild          Guild for lookup
     * @param user           User for lookup.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return global score of user
     */
    public static int getFreePoints(Guild guild, User user, MessageEventDataWrapper messageContext) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * from shepard_func.get_free_rubber_points(?,?)")) {
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
     * Add to all users 1 kudo.
     *
     * @return true if the query execution was successful
     */
    public static boolean upcountKudos() {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.upcount_free_rubber_points()")) {
            statement.execute();
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, null);
            return false;
        }
        return true;
    }


}