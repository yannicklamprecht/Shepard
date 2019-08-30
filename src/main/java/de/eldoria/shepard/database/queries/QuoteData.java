package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.database.DatabaseConnector;
import de.eldoria.shepard.database.types.Quote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static de.eldoria.shepard.database.DbUtil.handleException;

public final class QuoteData {

    private QuoteData() {
    }

    /**
     * Adds a new Quote.
     *
     * @param guild Guild for which the quote should be added.
     * @param quote quote to add
     * @param event event from command sending for error handling. Can be null.
     */
    public static void addQuote(Guild guild, String quote, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.add_quote(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setString(2, quote);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Alters a quote by id on guild.
     *
     * @param guild   Guild object for lookup
     * @param quoteId id on guild.
     * @param quote new quote
     * @param event   event from command sending for error handling. Can be null.
     */
    public static void alterQuote(Guild guild, int quoteId, String quote, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.alter_quote(?,?,?)")) {
            statement.setString(1, guild.getId());
            statement.setInt(2, quoteId);
            statement.setString(3, quote);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Removes a quote by id on guild.
     *
     * @param guild   Guild object for lookup
     * @param quoteId id on guild.
     * @param event   event from command sending for error handling. Can be null.
     */
    public static void removeQuote(Guild guild, int quoteId, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_quote(?,?)")) {
            statement.setString(1, guild.getId());
            statement.setInt(2, quoteId);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    /**
     * Gets all quotes from guild.
     *
     * @param guild Guild object for lookup
     * @param event event from command sending for error handling. Can be null.
     * @return List of Quote objects
     */
    public static List<Quote> getQuotes(Guild guild, MessageReceivedEvent event) {
        List<Quote> quotes = new ArrayList<>();
        try (PreparedStatement statement = DatabaseConnector.getConn()
                .prepareStatement("SELECT shepard_func.remove_quote(?,?)")) {
            statement.setString(1, guild.getId());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                quotes.add(new Quote(result.getString("quote"), result.getInt("quote_id")));
            }


        } catch (SQLException e) {
            handleException(e, event);
        }
        return quotes;
    }
}
