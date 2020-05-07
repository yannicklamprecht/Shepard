package de.eldoria.shepard.commandmodules.quote.data;

import de.eldoria.shepard.commandmodules.quote.types.QuoteElement;
import de.eldoria.shepard.database.QueryObject;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
     * @return true if the query execution was successful
     */
    public boolean addQuote(Guild guild, String quote, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.add_quote(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, quote);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, messageContext);
            return false;
        }
        return true;
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
    public boolean alterQuote(Guild guild, int quoteId, String quote, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.alter_quote(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setInt(2, quoteId);
            statement.setString(3, quote);
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
    public boolean removeQuote(Guild guild, int quoteId, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.remove_quote(?,?)")) {
            statement.setString(1, guild.getId());
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
    public List<QuoteElement> getQuotes(Guild guild, MessageEventDataWrapper messageContext) {
        List<QuoteElement> quotes = new ArrayList<>();
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_quotes(?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                quotes.add(new QuoteElement(result.getString("quote"), result.getInt("quote_id")));
            }
        } catch (SQLException e) {
            handleException(e, messageContext);
        }
        return quotes;
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
                                                 MessageEventDataWrapper messageContext) {
        List<QuoteElement> quotes = new ArrayList<>();
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT * from shepard_func.get_quotes_by_keyword(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, keyword);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                quotes.add(new QuoteElement(result.getString("quote"), result.getInt("quote_id")));
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
    public int getQuotesCount(Guild guild, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.get_quote_count(?)")) {
            statement.setString(1, guild.getId());
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
    public int getQuotesCountByKeyword(Guild guild, String keyword, MessageEventDataWrapper messageContext) {
        try (var conn = source.getConnection(); PreparedStatement statement = conn
                .prepareStatement("SELECT shepard_func.get_quote_count_by_keyword(?,?)")) {
            statement.setString(1, guild.getId());
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
}
