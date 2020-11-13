package de.eldoria.shepard.commandmodules.quote.commands;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.quote.data.QuoteData;
import de.eldoria.shepard.commandmodules.quote.types.QuoteElement;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static de.eldoria.shepard.localization.enums.commands.fun.QuoteLocale.*;

/**
 * Command which draws a random quote from database or a quote containing a keyword.
 */
@CommandUsage(EventContext.GUILD)
public class Quote extends QuoteCommand implements Executable, ReqDataSource {

    private QuoteData quoteData;

    /**
     * Create a new quote command object.
     */
    public Quote() {
        super("quote",
                null,
                DESCRIPTION.tag,
                SubCommand.builder("quote")
                        .addSubcommand(null,
                                Parameter.createInput("command.general.argument.keyword", A_EMPTY_OR_WORD.tag, false))
                        .build(),
                DESCRIPTION.tag,
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        List<QuoteElement> quotes;
        if (args.length > 0) {
            quotes = quoteData.getQuotesByKeyword(wrapper.getGuild().get(),
                    String.join(" ", Arrays.copyOfRange(args, 0, args.length)), wrapper);

        } else {
            quotes = quoteData.getQuotes(wrapper.getGuild().get(), wrapper);
            if (quotes.size() == 0) {
                MessageSender.sendMessage(M_NO_QUOTE_DEFINED.tag, wrapper.getMessageChannel());
                return;
            }
        }

        if (quotes.isEmpty()) {
            MessageSender.sendMessage(M_NO_QUOTE_FOUND.tag, wrapper.getMessageChannel());
            return;
        }

        Random rand = new Random();
        int i = rand.nextInt(quotes.size());
        QuoteElement quote = quotes.get(i);

        sendQuote(wrapper.getMessageChannel(), quote);
    }

    @Override
    public void addDataSource(DataSource source) {
        quoteData = new QuoteData(source);
    }
}
