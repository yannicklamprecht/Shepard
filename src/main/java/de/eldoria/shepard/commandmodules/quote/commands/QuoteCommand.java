package de.eldoria.shepard.commandmodules.quote.commands;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.quote.types.QuoteElement;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

import javax.annotation.Nullable;
import java.time.LocalDateTime;

public abstract class QuoteCommand extends Command {
    protected QuoteCommand(String commandName, String[] commandAliases, String commandDesc, SubCommand[] subCommands, String standaloneDescription, CommandCategory category) {
        super(commandName, commandAliases, commandDesc, subCommands, standaloneDescription, category);
    }

    protected QuoteCommand(String commandName, String[] commandAliases, String commandDesc, SubCommand[] subCommands, CommandCategory category) {
        super(commandName, commandAliases, commandDesc, subCommands, category);
    }

    protected QuoteCommand(String commandName, String[] commandAliases, String commandDesc, CommandCategory category) {
        super(commandName, commandAliases, commandDesc, category);
    }

    protected void sendQuote(MessageChannel channel, @Nullable QuoteElement quote) {
        if (quote == null) return;
        EmbedBuilder embed = new EmbedBuilder().setAuthor("#" + quote.getQuoteId())
                .setDescription(quote.getQuote());
        if (quote.getCreated().isAfter(LocalDateTime.parse("2000-01-01T00:00:00"))) {
            embed.setTimestamp(quote.getCreated());
        }
        if (quote.getSource() != null) {
            embed.setFooter(String.valueOf(quote.getSource()));
        }

        channel.sendMessage(embed.build()).queue();
    }
}
