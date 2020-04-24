package de.eldoria.shepard.webapi.data;

import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.webapi.apiobjects.ApiRank;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.database.DbUtil.handleException;

public class KudoData extends QueryObject {
    private final JDA jda;

    /**
     * Create a new Kudo data object.
     *
     * @param jda    jda instance
     * @param source data source for information retrieval
     */
    public KudoData(JDA jda, DataSource source) {
        super(source);
        this.jda = jda;
    }

    /**
     * Get the global kudo ranking.
     *
     * @param page     page of ranking
     * @param pagesize size of page
     * @return sorted list of ranking objects
     */
    public List<ApiRank> getGlobalRanking(int page, int pagesize) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * FROM shepard_api.kudos_globaluser(?,?)")) {
            statement.setInt(1, page);
            statement.setInt(2, pagesize);
            return getRanking(page, pagesize, statement);
        } catch (SQLException e) {
            handleException(e, null);
            return Collections.emptyList();
        }
    }

    /**
     * Get amount of pages available for global ranking.
     *
     * @param pagesize size of pages.
     * @return amount of pages which can be requested
     */
    public int getGlobalRankingPagecount(int pagesize) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_api.kudos_globaluser_pagecount(?)")) {
            statement.setInt(1, pagesize);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
            return 0;
        }
        return 0;
    }

    /**
     * Get the global kudo ranking filtered by players.
     *
     * @param users    users for filtering
     * @param page     page of ranking
     * @param pagesize size of page
     * @return sorted and filtered list of ranking objects
     */
    public List<ApiRank> getGlobalRankingFilter(List<User> users, int page, int pagesize) {
        List<ApiRank> result = new ArrayList<>();
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * FROM shepard_api.kudos_globaluser_filter(?,?,?)")) {
            Long[] userIds = new Long[users.size()];
            users.stream().map(ISnowflake::getIdLong).collect(Collectors.toList()).toArray(userIds);
            Array ids = conn.createArrayOf("bigint", userIds);
            statement.setArray(1, ids);
            statement.setInt(2, page);
            statement.setInt(3, pagesize);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = jda.getUserById(resultSet.getLong("user_id"));
                if (user == null) {
                    result.add(
                            new ApiRank(jda,
                                    resultSet.getInt("rank"),
                                    resultSet.getLong("user_id"),
                                    resultSet.getLong("score")));
                    continue;
                }
                result.add(new ApiRank(resultSet.getInt("rank"), user, resultSet.getLong("score")));
            }
            return result;
        } catch (SQLException e) {
            handleException(e, null);
            return Collections.emptyList();
        }
    }

    /**
     * Get the pagecount of a filtered global ranking.
     *
     * @param users    users for filtering
     * @param pagesize size of pages.
     * @return amount of pages which can be requested
     */
    public int getGlobalRankingFilterPagecount(List<User> users, int pagesize) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_api.kudos_globaluser_filter_pagecount(?,?)")) {
            Long[] userIds = new Long[users.size()];
            users.stream().map(ISnowflake::getIdLong).collect(Collectors.toList()).toArray(userIds);
            Array ids = conn.createArrayOf("bigint", userIds);
            statement.setArray(1, ids);
            statement.setInt(2, pagesize);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
            return 0;
        }
        return 0;
    }

    /**
     * Get the global kudo ranking.
     *
     * @param guild    guild for filtering
     * @param page     page of ranking
     * @param pagesize size of pages.
     * @return sorted list of ranking objects
     */
    public List<ApiRank> getGuildRanking(long guild, int page, int pagesize) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * FROM shepard_api.kudos_guild(?,?,?)")) {
            statement.setLong(1, guild);
            statement.setInt(2, page);
            statement.setInt(3, pagesize);
            return getRanking(page, pagesize, statement);
        } catch (SQLException e) {
            handleException(e, null);
            return Collections.emptyList();
        }
    }

    /**
     * Get amount of pages available for guild ranking.
     *
     * @param guild    guild for filtering
     * @param pagesize size of pages.
     * @return amount of pages which can be requested
     */
    public int getGuildRankingPagecount(long guild, int pagesize) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_api.kudos_guild_pagecount(?,?)")) {
            statement.setLong(1, guild);
            statement.setInt(2, pagesize);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
            return 0;
        }
        return 0;
    }

    /**
     * Get the guild kudo ranking filtered by players.
     *
     * @param users    users for filtering
     * @param guild    guild for filtering
     * @param page     page of ranking
     * @param pagesize size of page
     * @return sorted and filtered list of ranking objects
     */
    public List<ApiRank> getGuildRankingFilter(List<User> users, Long guild, int page, int pagesize) {
        List<ApiRank> result = new ArrayList<>();

        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * FROM shepard_api.kudos_guild_filter(?,?,?,?)")) {
            Long[] userIds = new Long[users.size()];
            users.stream().map(ISnowflake::getIdLong).collect(Collectors.toList()).toArray(userIds);
            Array ids = source.getConnection().createArrayOf("bigint", userIds);
            statement.setArray(1, ids);
            statement.setLong(2, guild);
            statement.setInt(3, page);
            statement.setInt(4, pagesize);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = jda.getUserById(resultSet.getLong("user_id"));
                if (user == null) {
                    result.add(
                            new ApiRank(jda,
                                    resultSet.getInt("rank"),
                                    resultSet.getLong("user_id"),
                                    resultSet.getLong("score")));
                    continue;
                }
                result.add(new ApiRank(resultSet.getInt("rank"), user, resultSet.getLong("score")));
            }
            return result;
        } catch (SQLException e) {
            handleException(e, null);
            return Collections.emptyList();
        }
    }

    /**
     * Get amount of pages available for filtered guild ranking.
     *
     * @param users    users for filtering
     * @param guild    guild for filtering
     * @param pagesize size of pages.
     * @return amount of pages which can be requested
     */
    public int getGuildRankingFilterPagecount(List<User> users, long guild, int pagesize) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_api.kudos_guild_filter_pagecount(?,?,?)")) {
            Long[] userIds = new Long[users.size()];
            users.stream().map(ISnowflake::getIdLong).collect(Collectors.toList()).toArray(userIds);
            Array ids = conn.createArrayOf("bigint", userIds);
            statement.setArray(1, ids);
            statement.setLong(2, guild);
            statement.setInt(3, pagesize);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            handleException(e, null);
            return 0;
        }
        return 0;
    }

    /**
     * Get the list from a ranking statement.
     *
     * @param page      page of ranking
     * @param pagesize  size of page
     * @param statement statement to process
     * @return sorted list of ranking objects
     * @throws SQLException if the statement failed
     */
    private List<ApiRank> getRanking(int page, int pagesize, PreparedStatement statement) throws SQLException {
        List<ApiRank> result = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery();
        int rank = (page - 1) * pagesize;
        while (resultSet.next()) {
            long score = resultSet.getLong("score");
            rank++;
            User user = jda.getUserById(resultSet.getLong("user_id"));
            if (user == null) {
                result.add(new ApiRank(jda, rank, resultSet.getLong("user_id"), score));
                continue;
            }
            result.add(new ApiRank(rank, user, score));
        }
        return result;
    }
}
