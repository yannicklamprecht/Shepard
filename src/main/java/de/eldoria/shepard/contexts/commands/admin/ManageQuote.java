package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.queries.QuoteData;
import de.eldoria.shepard.database.types.QuoteElement;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public void internalExecute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String cmd = args[0];

        if (cmd.equalsIgnoreCase("add") || cmd.equalsIgnoreCase("a")) {
            addQuote(args, receivedEvent);
            return;
        }

        if (cmd.equalsIgnoreCase("remove") || cmd.equalsIgnoreCase("r")) {
            removeQuote(args, receivedEvent);
            return;
        }

        if (cmd.equalsIgnoreCase("list") || cmd.equalsIgnoreCase("l")) {
            showQuotes(args, receivedEvent);
            return;
        }

        if (cmd.equalsIgnoreCase("alter") || cmd.equalsIgnoreCase("alt")) {
            if (args.length < 3) {
                MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, receivedEvent.getChannel());
            }

            int quoteId;
            int quotesCount = QuoteData.getQuotesCount(receivedEvent.getGuild(), receivedEvent);
            try {
                quoteId = Integer.parseInt(args[1]);
            } catch (IllegalArgumentException e) {
                MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, receivedEvent.getChannel());
                return;
            }

            if (quoteId > quotesCount) {
                MessageSender.sendSimpleError(ErrorType.INVALID_ID, receivedEvent.getChannel());
            }

            String quote = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

            QuoteData.alterQuote(receivedEvent.getGuild(), quoteId, quote, receivedEvent);

            MessageSender.sendSimpleTextBox("Changed text of quote with id **" + quoteId + "**",
                    quote, Color.blue, receivedEvent.getChannel());
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, receivedEvent.getChannel());
    }

    private void showQuotes(String[] args, MessageReceivedEvent receivedEvent) {
        List<QuoteElement> quotes;
        if (args.length > 1) {
            quotes = QuoteData.getQuotesByKeyword(receivedEvent.getGuild(),
                    String.join(" ", Arrays.copyOfRange(args, 1, args.length)), receivedEvent);
        } else {
            quotes = QuoteData.getQuotes(receivedEvent.getGuild(), receivedEvent);
        }

        if (quotes.size() == 0) {
            MessageSender.sendMessage("No quotes set on this guild.", receivedEvent.getChannel());
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
            MessageSender.sendMessage(message, receivedEvent.getChannel());
        }
    }

    private void removeQuote(String[] args, MessageReceivedEvent receivedEvent) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, receivedEvent.getChannel());
        }

        int quoteId;
        int quotesCount = QuoteData.getQuotesCount(receivedEvent.getGuild(), receivedEvent);
        try {
            quoteId = Integer.parseInt(args[1]);
        } catch (IllegalArgumentException e) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, receivedEvent.getChannel());
            return;
        }

        if (quoteId > quotesCount) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ID, receivedEvent.getChannel());
        }

        QuoteData.removeQuote(receivedEvent.getGuild(), quoteId, receivedEvent);
        MessageSender.sendSimpleTextBox("Remove quote with id **" + quoteId + "**",
                "", Color.red, receivedEvent.getChannel());
    }

    private void addQuote(String[] args, MessageReceivedEvent receivedEvent) {
        if (args.length == 1) {
            MessageSender.sendSimpleError(ErrorType.NO_QUOTE_FOUND, receivedEvent.getChannel());
            return;
        }

        String quote = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        QuoteData.addQuote(receivedEvent.getGuild(), quote, receivedEvent);
        MessageSender.sendSimpleTextBox("Saved Quote!", quote, Color.green, receivedEvent.getChannel());
    }
}
