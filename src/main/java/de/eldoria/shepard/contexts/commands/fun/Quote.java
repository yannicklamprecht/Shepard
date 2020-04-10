package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArgument;
import de.eldoria.shepard.contexts.commands.argument.SubArgument;
import de.eldoria.shepard.database.queries.QuoteData;
import de.eldoria.shepard.database.types.QuoteElement;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static de.eldoria.shepard.localization.enums.commands.fun.QuoteLocale.A_EMPTY_OR_WORD;
import static de.eldoria.shepard.localization.enums.commands.fun.QuoteLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.fun.QuoteLocale.M_NO_QUOTE_DEFINED;
import static de.eldoria.shepard.localization.enums.commands.fun.QuoteLocale.M_NO_QUOTE_FOUND;

/**
 * Command which draws a random quote from database or a quote containing a keyword.
 */
public class Quote extends Command {

    /**
     * Create a new quote command object.
     */
    public Quote() {
        commandName = "Quote";
        commandDesc = DESCRIPTION.tag;
        commandArguments = new CommandArgument[] {
                new CommandArgument("keyword", false,
                        new SubArgument("keyword", A_EMPTY_OR_WORD.tag))
        };
        category = ContextCategory.FUN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        List<QuoteElement> quotes;
        if (args.length > 0) {
            quotes = QuoteData.getQuotesByKeyword(messageContext.getGuild(),
                    String.join(" ", Arrays.copyOfRange(args, 0, args.length)), messageContext);

        } else {
            quotes = QuoteData.getQuotes(messageContext.getGuild(), messageContext);
            if (quotes.size() == 0) {
                MessageSender.sendMessage(M_NO_QUOTE_DEFINED.tag, messageContext.getTextChannel());
                return;
            }
        }

        if (quotes.size() == 0) {
            MessageSender.sendMessage(M_NO_QUOTE_FOUND.tag, messageContext.getTextChannel());
            return;
        }

        Random rand = new Random();
        int i = rand.nextInt(quotes.size());

        MessageSender.sendMessage(quotes.get(i).getQuote(), messageContext.getTextChannel());
    }
}
