package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.queries.QuoteData;
import de.eldoria.shepard.database.types.QuoteElement;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class ManageQuote extends Command {

    /**
     * Create a new manage quote command object.
     */
    public ManageQuote() {
        commandName = "manageQuotes";
        commandAliases = new String[] {"mq"};
        commandDesc = "add or remove quotes";
        commandArgs = new CommandArg[] {
                new CommandArg("action",
                        "**__a__dd** -> Adds a quote" + lineSeparator()
                                + "**__alt__er** -> Changes the text of a quote" + lineSeparator()
                                + "**__r__emove** -> Removes a Quote" + lineSeparator()
                                + "**__l__ist** -> Lists all Quotes with index",
                        true),
                new CommandArg("action",
                        "**add** -> [Quote]" + lineSeparator()
                                + "**alter** -> [Quote id to change] [test]" + lineSeparator()
                                + "**remove** -> [Quote id to remove]" + lineSeparator()
                                + "**show** -> [keyword] shows all quotes which contain the keyword or"
                                + "leave empty to show all quotes",
                        false)};
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];

        if (isArgument(cmd, "add", "a")) {
            addQuote(args, messageContext);
            return;
        }

        if (isArgument(cmd, "remove", "r")) {
            removeQuote(args, messageContext);
            return;
        }

        if (isArgument(cmd, "list", "l")) {
            showQuotes(args, messageContext);
            return;
        }

        if (isArgument(cmd, "alter", "alt")) {
            if (args.length < 3) {
                MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getChannel());
            }

            int quoteId = verifyId(args[1], messageContext);

            if (quoteId == -1) {
                return;
            }

            String quote = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

            if (QuoteData.alterQuote(messageContext.getGuild(), quoteId, quote, messageContext)) {
                MessageSender.sendSimpleTextBox("Changed text of quote with id **" + quoteId + "**",
                        quote, Color.blue, messageContext.getChannel());
            }
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getChannel());
    }

    private void showQuotes(String[] args, MessageEventDataWrapper messageContext) {
        List<QuoteElement> quotes;
        if (args.length > 1) {
            quotes = QuoteData.getQuotesByKeyword(messageContext.getGuild(),
                    String.join(" ", Arrays.copyOfRange(args, 1, args.length)), messageContext);

        } else {
            quotes = QuoteData.getQuotes(messageContext.getGuild(), messageContext);
        }

        if (quotes.size() == 0) {
            MessageSender.sendMessage("No quotes set on this guild.", messageContext.getChannel());
        }

        List<String> quoteStrings = new ArrayList<>();

        for (QuoteElement quote : quotes) {
            quoteStrings.add(quote.getQuoteId() + " -> " + quote.getQuote() + lineSeparator());
        }

        List<String> messageFragments = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        for (String quote : quoteStrings) {
            if (builder.length() + quote.length() > 2000) {
                messageFragments.add(builder.toString());
                builder.setLength(0);
            }
            builder.append(quote);
        }
        messageFragments.add(builder.toString());


        for (String message : messageFragments) {
            MessageSender.sendMessage(message, messageContext.getChannel());
        }
    }

    private void removeQuote(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getChannel());
        }

        int quoteId = verifyId(args[1], messageContext);

        if (quoteId == -1) {
            return;
        }

        if (QuoteData.removeQuote(messageContext.getGuild(), quoteId, messageContext)) {
            MessageSender.sendSimpleTextBox("Remove quote with id **" + quoteId + "**",
                    "", Color.red, messageContext.getChannel());
        }
    }

    private void addQuote(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 1) {
            MessageSender.sendSimpleError(ErrorType.NO_QUOTE_FOUND, messageContext.getChannel());
            return;
        }

        String quote = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        if (QuoteData.addQuote(messageContext.getGuild(), quote, messageContext)) {
            MessageSender.sendSimpleTextBox("Saved Quote!", quote, Color.green, messageContext.getChannel());
        }
    }

    /**
     * Returns the id from a string.
     *
     * @param number         string to parse
     * @param messageContext message context for error logging
     * @return -1 when the string is not a number or the number is <0 or larger than the amount of quotes.
     */
    private int verifyId(String number, MessageEventDataWrapper messageContext) {
        int quotesCount = QuoteData.getQuotesCount(messageContext.getGuild(), messageContext);
        int quoteId = -1;
        try {
            quoteId = Integer.parseInt(number);
        } catch (IllegalArgumentException e) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext.getChannel());
            return quoteId;
        }

        if (quoteId > quotesCount || quoteId < 0) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ID, messageContext.getChannel());
        }
        return quoteId;
    }
}
