package de.eldoria.shepard.database.queries.api;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.database.types.ApiRank;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.eldoria.shepard.database.DatabaseConnector.getConn;
import static de.eldoria.shepard.database.DbUtil.handleExceptionAndIgnore;

public class KudosData {
    public static List<ApiRank> getGlobalRanking(int page, int pagesize) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * FROM shepard_api.kudos_globaluser(?,?)")) {
            statement.setInt(1, page);
            statement.setInt(2, pagesize);
            return getRanking(page, pagesize, statement);
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, null);
            return Collections.emptyList();
        }
    }

    public static int getGlobalRankingPagecount(int pagesize) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_api.kudos_globaluser_pagecount(?)")) {
            statement.setInt(1, pagesize);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, null);
            return 0;
        }
        return 0;
    }

    public static List<ApiRank> getGlobalRankingFilter(List<User> users, int page, int pagesize) {
        List<ApiRank> result = new ArrayList<>();
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * FROM shepard_api.kudos_globaluser_filter(?,?,?)")) {
            Long[] userIds = (Long[]) users.stream().map(ISnowflake::getIdLong).toArray();
            Array ids = getConn().createArrayOf("bigint", userIds);
            statement.setArray(1, ids);
            statement.setInt(2, page);
            statement.setInt(3, pagesize);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = ShepardBot.getJDA().getUserById(resultSet.getLong("user_id"));
                if (user == null) {
                    result.add(new ApiRank(resultSet.getInt("rank"), resultSet.getLong("user_id"), resultSet.getLong("score")));
                    continue;
                }
                result.add(new ApiRank(resultSet.getInt("rank"), user, resultSet.getLong("score")));
            }
            return result;
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, null);
            return Collections.emptyList();
        }
    }

    public static int getGlobalRankingFilterPagecount(List<User> users, int pagesize) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_api.kudos_globaluser_filter_pagecount(?,?)")) {
            Long[] userIds = (Long[]) users.stream().map(ISnowflake::getIdLong).toArray();
            Array ids = getConn().createArrayOf("bigint", userIds);
            statement.setArray(1, ids);
            statement.setInt(2, pagesize);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, null);
            return 0;
        }
        return 0;
    }

    public static List<ApiRank> getGuildRanking(long guild, int page, int pagesize) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * FROM shepard_api.kudos_guild(?,?,?)")) {
            statement.setLong(1, guild);
            statement.setInt(2, page);
            statement.setInt(3, pagesize);
            return getRanking(page, pagesize, statement);
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, null);
            return Collections.emptyList();
        }
    }

    public static int getGuildRankingPagecount(long guild, int pagesize) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_api.kudos_guild_pagecount(?,?)")) {
            statement.setLong(1, guild);
            statement.setInt(2, pagesize);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, null);
            return 0;
        }
        return 0;
    }

    public static List<ApiRank> getGuildRankingFilter(List<User> users, Long guild, int page, int pagesize) {
        List<ApiRank> result = new ArrayList<>();

        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT * FROM shepard_api.kudos_guild_filter(?,?,?,?)")) {
            Long[] userIds = (Long[]) users.stream().map(ISnowflake::getIdLong).toArray();
            Array ids = getConn().createArrayOf("bigint", userIds);
            statement.setArray(1, ids);
            statement.setLong(2, guild);
            statement.setInt(3, page);
            statement.setInt(4, pagesize);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = ShepardBot.getJDA().getUserById(resultSet.getLong("user_id"));
                if (user == null) {
                    result.add(new ApiRank(resultSet.getInt("rank"), resultSet.getLong("user_id"), resultSet.getLong("score")));
                    continue;
                }
                result.add(new ApiRank(resultSet.getInt("rank"), user, resultSet.getLong("score")));
            }
            return result;
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, null);
            return Collections.emptyList();
        }
    }

    public static int getGuildRankingFilterPagecount(List<User> users, long guild, int pagesize) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_api.kudos_globaluser_filter_pagecount(?,?,?)")) {
            Long[] userIds = (Long[]) users.stream().map(ISnowflake::getIdLong).toArray();
            Array ids = getConn().createArrayOf("bigint", userIds);
            statement.setArray(1, ids);
            statement.setLong(2, guild);
            statement.setInt(3, pagesize);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            handleExceptionAndIgnore(e, null);
            return 0;
        }
        return 0;
    }

    private static List<ApiRank> getRanking(int page, int pagesize, PreparedStatement statement) throws SQLException {
        List<ApiRank> result = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery();
        int rank = (page - 1) * pagesize;
        while (resultSet.next()) {
            String scoreS = resultSet.getString("score");
            long score = ArgumentParser.parseLong(scoreS);
            rank++;
            User user = ShepardBot.getJDA().getUserById(resultSet.getLong("user_id"));
            if (user == null) {
                result.add(new ApiRank(rank, resultSet.getLong("user_id"), score));
                continue;
            }
            result.add(new ApiRank(rank, user, score));
        }
        return result;
    }
}
