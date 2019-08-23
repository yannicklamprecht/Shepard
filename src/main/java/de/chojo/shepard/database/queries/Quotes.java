package de.chojo.shepard.database.queries;

import de.chojo.shepard.database.DatabaseConnector;
import de.chojo.shepard.database.types.Quote;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static de.chojo.shepard.database.DbUtil.handleException;

public class Quotes {
    public static void addQuote(String guildId, String quote, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.add_quote(?,?)")) {
            statement.setString(1, guildId);
            statement.setString(2, quote);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static void alterQuote(String guildId, int quoteId, String quote, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.alter_quote(?,?,?)")) {
            statement.setString(1, guildId);
            statement.setInt(2, quoteId);
            statement.setString(3, quote);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static void removeQuote(String guildId, int quoteId, MessageReceivedEvent event) {
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_quote(?,?)")) {
            statement.setString(1, guildId);
            statement.setInt(2, quoteId);
            statement.execute();
        } catch (SQLException e) {
            handleException(e, event);
        }
    }

    public static List<Quote> getQuotes(String guildId, MessageReceivedEvent event) {
        List<Quote> quotes = new ArrayList<>();
        try (PreparedStatement statement = DatabaseConnector.getConn().
                prepareStatement("SELECT shepard_func.remove_quote(?,?)")) {
            statement.setString(1, guildId);
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
