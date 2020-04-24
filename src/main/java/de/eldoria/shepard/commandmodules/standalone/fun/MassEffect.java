package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.enums.commands.fun.MassEffectLocale;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
public class MassEffect extends Command implements Executable {
    private final List<String> quotes;

    /**
     * Create a new mass effect quote command.
     */
    public MassEffect() {
        super("massEffect",
                new String[] {"shepQuote"},
                MassEffectLocale.DESCRIPTION.tag,
                SubCommand.builder("massEffect")
                        .addSubcommand(null,
                                Parameter.createInput(GeneralLocale.A_KEYWORD.tag, GeneralLocale.AD_KEYWORD.tag, false))
                        .build(),
                MassEffectLocale.DESCRIPTION.tag,
                CommandCategory.FUN);

        Yaml yaml = new Yaml(new Constructor(QuoteList.class));

        quotes = new ArrayList<>();
        try {
            QuoteList quoteList = yaml.load(getClass().getResourceAsStream("/mass_effect_1.yml"));
            quotes.addAll(Arrays.asList(quoteList.quotes));
            quoteList = yaml.load(getClass().getResourceAsStream("/mass_effect_2.yml"));
            quotes.addAll(Arrays.asList(quoteList.quotes));
            quoteList = yaml.load(getClass().getResourceAsStream("/mass_effect_3.yml"));
            quotes.addAll(Arrays.asList(quoteList.quotes));
        } catch (Exception e) {
            log.error("Could not load mass effect quotes", e);
        }
        log.info("Loaded " + quotes.size() + " mass effect quotes.");
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        List<String> filteredQuotes;
        if (args.length != 0) {
            filteredQuotes = quotes.stream()
                    .filter(q -> q.toLowerCase().contains(ArgumentParser.getMessage(args, 0).toLowerCase()))
                    .collect(Collectors.toList());
            if (filteredQuotes.size() == 0) {
                MessageSender.sendSimpleError(ErrorType.NO_QUOTE_FOUND, messageContext.getTextChannel());
                return;
            }
        } else {
            filteredQuotes = quotes;
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Mass Effect Quote")
                .setDescription(filteredQuotes.get(new Random().nextInt(filteredQuotes.size())));
        messageContext.getChannel().sendMessage(builder.build()).queue();
    }

    @Data
    public static class QuoteList {
        private String[] quotes = null;
    }
}
