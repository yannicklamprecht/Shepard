package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.queries.QuoteData;
import de.eldoria.shepard.database.types.QuoteElement;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Quote extends Command {

    /**
     * Create a new quote command object.
     */
    public Quote() {
        commandName = "Quote";
        commandDesc = "Get a random quote or a quote with a keyword";
        commandArgs = new CommandArg[] {
                new CommandArg("keyword",
                        "leave empty or enter a keyword to get a quote containing this word",
                        false)};
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        List<QuoteElement> quotes;
        if (args.length > 0) {
            quotes = QuoteData.getQuotesByKeyword(messageContext.getGuild(),
                    String.join(" ", Arrays.copyOfRange(args, 0, args.length)), messageContext);

        } else {
            quotes = QuoteData.getQuotes(messageContext.getGuild(), messageContext);
        }

        if (quotes.size() == 0) {
            MessageSender.sendMessage("No quote found!", messageContext.getChannel());
            return;
        }

        Random rand = new Random();
        int i = rand.nextInt(quotes.size());

        MessageSender.sendMessage(quotes.get(i).getQuote(), messageContext.getChannel());
    }
}
