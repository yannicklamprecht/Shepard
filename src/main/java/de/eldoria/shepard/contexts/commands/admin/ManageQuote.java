package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.QuoteData;
import de.eldoria.shepard.database.types.QuoteElement;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.GeneralLocale.A_TEXT;
import static de.eldoria.shepard.localization.enums.admin.ManageQuoteLocale.A_KEYWORD;
import static de.eldoria.shepard.localization.enums.admin.ManageQuoteLocale.A_QUOTE_ID;
import static de.eldoria.shepard.localization.enums.admin.ManageQuoteLocale.C_ADD;
import static de.eldoria.shepard.localization.enums.admin.ManageQuoteLocale.C_ALTER;
import static de.eldoria.shepard.localization.enums.admin.ManageQuoteLocale.C_LIST;
import static de.eldoria.shepard.localization.enums.admin.ManageQuoteLocale.C_REMOVE;
import static de.eldoria.shepard.localization.enums.admin.ManageQuoteLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.admin.ManageQuoteLocale.M_CHANGED_QUOTE;
import static de.eldoria.shepard.localization.enums.admin.ManageQuoteLocale.M_NO_QUOTES;
import static de.eldoria.shepard.localization.enums.admin.ManageQuoteLocale.M_REMOVED_QUOTE;
import static de.eldoria.shepard.localization.enums.admin.ManageQuoteLocale.M_SAVED_QUOTE;
import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class ManageQuote extends Command {

    /**
     * Create a new manage quote command object.
     */
    public ManageQuote() {
        commandName = "manageQuotes";
        commandAliases = new String[] {"mq"};
        commandDesc = DESCRIPTION.replacement;
        commandArgs = new CommandArg[] {
                new CommandArg("action", true,
                        new SubArg("add", C_ADD.replacement, true),
                        new SubArg("alter", C_ALTER.replacement, true),
                        new SubArg("remove", C_REMOVE.replacement, true),
                        new SubArg("list", C_LIST.replacement, true)),
                new CommandArg("action", false,
                        new SubArg("add", A_TEXT.replacement),
                        new SubArg("alter", A_QUOTE_ID + " " + A_TEXT),
                        new SubArg("remove", A_QUOTE_ID.replacement),
                        new SubArg("show", A_KEYWORD.replacement))
        };
        category = ContextCategory.ADMIN;
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
                MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
            }

            int quoteId = verifyId(args[1], messageContext);

            if (quoteId == -1) {
                return;
            }

            String quote = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

            if (QuoteData.alterQuote(messageContext.getGuild(), quoteId, quote, messageContext)) {
                MessageSender.sendSimpleTextBox(locale.getReplacedString(M_CHANGED_QUOTE.localeCode,
                        messageContext.getGuild(),
                        "**" + quoteId + "**"),
                        quote, Color.blue, messageContext);
            }
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext);
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
            MessageSender.sendMessage(M_NO_QUOTES.replacement, messageContext);
        }

        String message = quotes.stream()
                .map(quote -> "**" + quote.getQuoteId() + "** -> " + quote.getQuote() + lineSeparator())
                .collect(Collectors.joining());
        MessageSender.sendMessage(message, messageContext);
    }

    private void removeQuote(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
        }

        int quoteId = verifyId(args[1], messageContext);

        if (quoteId == -1) {
            return;
        }

        if (QuoteData.removeQuote(messageContext.getGuild(), quoteId, messageContext)) {
            MessageSender.sendMessage(locale.getReplacedString(M_REMOVED_QUOTE.localeCode,
                    messageContext.getGuild(), "**" + quoteId + "**"), messageContext);
        }
    }

    private void addQuote(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 1) {
            MessageSender.sendSimpleError(ErrorType.NO_QUOTE_FOUND, messageContext);
            return;
        }

        String quote = ArgumentParser.getMessage(args, 1);

        if (QuoteData.addQuote(messageContext.getGuild(), quote, messageContext)) {
            MessageSender.sendSimpleTextBox(M_SAVED_QUOTE.replacement, quote, Color.green, messageContext);
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
        Integer quoteId = ArgumentParser.parseInt(number);
        if (quoteId == null) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext);
            return -1;
        }

        if (quoteId > quotesCount || quoteId < 0) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ID, messageContext);
            return -1;
        }
        return quoteId;
    }
}
