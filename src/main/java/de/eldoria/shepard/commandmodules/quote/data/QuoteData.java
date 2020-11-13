package de.eldoria.shepard.commandmodules.quote.data;

import de.eldoria.shepard.commandmodules.quote.types.QuoteElement;
import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static de.eldoria.shepard.database.DbUtil.handleException;

public final class QuoteData extends QueryObject {

    /**
     * Create a new quote data object.
     *
     * @param source data source for information retrieval
     */
    public QuoteData(DataSource source) {
        super(source);
    }

    /**
     * Adds a new Quote.
     *
     * @param guild          Guild for which the quote should be added.
     * @param quote          quote to add
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return returns the global quote id or -1 if transaction failed
     */
    public int addQuote(Guild guild, String quote, String quoteSource, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * FROM shepard_func.add_quote(?,?,?)")) {
            statement.setLong(1, guild.getIdLong());
            statement.setString(2, quote);
            statement.setString(3, quoteSource);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
            return -1;
        }
        return -1;
    }

    /**
     * Alters a quote by id on guild.
     *
     * @param guild          Guild object for lookup
     * @param quoteId        id on guild.
     * @param quote          new quote
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean alterQuote(Guild guild, int quoteId, @Nullable String quote, @Nullable String quoteSource, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.alter_quote(?,?,?,?)")) {
            statement.setLong(1, guild.getIdLong());
            statement.setInt(2, quoteId);
            if (quote == null) {
                statement.setNull(3, Types.VARCHAR);
            } else {
                statement.setString(3, quote);
            }
            if (quoteSource == null) {
                statement.setNull(4, Types.VARCHAR);
            } else {
                statement.setString(4, quoteSource);
            }
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Removes a quote by id on guild.
     *
     * @param guild          Guild object for lookup
     * @param quoteId        id on guild.
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return true if the query execution was successful
     */
    public boolean removeQuote(Guild guild, int quoteId, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.remove_quote(?,?)")) {
            statement.setLong(1, guild.getIdLong());
            statement.setInt(2, quoteId);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
    }

    /**
     * Gets all quotes from guild.
     *
     * @param guild          Guild object for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return List of Quote objects
     */
    public List<QuoteElement> getQuotes(Guild guild, EventWrapper messageContext) {
        List<QuoteElement> quotes = new ArrayList<>();
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * FROM shepard_func.get_quotes(?)")) {
            statement.setLong(1, guild.getIdLong());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                quotes.add(toQuote(result));
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return quotes;
    }

    /**
     * Gets a quote..
     *
     * @param id             global quote id
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return List of Quote objects
     */
    public QuoteElement getQuote(int id, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * FROM shepard_func.get_quote(?)")) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return (toQuote(result));
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return null;
    }

    /**
     * Gets a quote from guild.
     *
     * @param id             guild quote id
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return List of Quote objects
     */
    public QuoteElement getQuote(Guild guild, int id, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * FROM shepard_func.get_quote(?,?)")) {
            statement.setLong(1, guild.getIdLong());
            statement.setInt(2, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return (toQuote(result));
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return null;
    }

    /**
     * Gets all quotes from guild.
     *
     * @param guild          Guild object for lookup
     * @param keyword        Keyword for lookup. Not case sensitive
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return List of Quote objects
     */
    public List<QuoteElement> getQuotesByKeyword(Guild guild, String keyword,
                                                 EventWrapper messageContext) {
        List<QuoteElement> quotes = new ArrayList<>();
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * FROM shepard_func.get_quotes_by_keyword(?,?)")) {
            statement.setLong(1, guild.getIdLong());
            statement.setString(2, keyword);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                quotes.add(toQuote(result));
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return quotes;
    }

    /**
     * Gets the number of quotes for a guild.
     *
     * @param guild          Guild object for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return List of Quote objects
     */
    public int getQuotesCount(Guild guild, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.get_quote_count(?)")) {
            statement.setLong(1, guild.getIdLong());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return 0;
    }

    /**
     * Gets the number of quotes for a guild which contains the keyword.
     *
     * @param guild          Guild object for lookup
     * @param keyword        Keyword not case sensitive
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return List of Quote objects
     */
    public int getQuotesCountByKeyword(Guild guild, String keyword, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.get_quote_count_by_keyword(?,?)")) {
            statement.setLong(1, guild.getIdLong());
            statement.setString(2, keyword);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return 0;
    }

    public long getQuoteChannel(Guild guild, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.get_quote_channel(?)")) {
            statement.setLong(1, guild.getIdLong());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getLong(1);
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return 0;
    }

    public boolean setQuoteChannel(Guild guild, TextChannel channel, EventWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.set_quote_channel(?,?)")) {
            statement.setLong(1, guild.getIdLong());
            if (channel == null) {
                statement.setNull(2, Types.BIGINT);
            } else {
                statement.setLong(2, channel.getIdLong());
            }
            statement.execute();
            return true;
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return false;
    }


    private QuoteElement toQuote(ResultSet result) throws SQLException {
        return new QuoteElement(
                result.getString("quote"),
                result.getInt("quote_id"),
                result.getString("source"),
                result.getTimestamp("created").toLocalDateTime(),
                result.getTimestamp("edited").toLocalDateTime());
    }
}
