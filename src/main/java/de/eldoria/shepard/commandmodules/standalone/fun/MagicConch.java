package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Random;

import static de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale.ANSWER_NEGATIVE;
import static de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale.ANSWER_NEUTRAL;
import static de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale.ANSWER_POSITIVE;
import static de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale.M_ANSWER;


/**
 * Command which provides magic conch sentences.
 */
public class MagicConch extends Command implements Executable {
    /**
     * Creates a new MagicConch command object.
     */
    public MagicConch() {
        super("magicConch",
                new String[] {"conch"},
                "command.magicConch.description",
                SubCommand.builder("conch")
                        .addSubcommand(null,
                                Parameter.createInput("command.general.argument.message", "command.general.argument.message", false))
                        .build(),
                "command.magicConch.description",
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String word;
        Random rand = new Random();
        int type = rand.nextInt(3);


        MagicConchLocale answer;
        switch (type) {
            case 0:
                answer = ANSWER_POSITIVE;
                break;
            case 1:
                answer = ANSWER_NEGATIVE;
                break;
            case 2:
                answer = ANSWER_NEUTRAL;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        String[] decisions;
        if (wrapper.isGuildEvent()) {
            decisions = locale.getLanguageString(wrapper.getGuild().get(), answer.localeCode).split("\\|");

        } else {
            decisions = locale.getLanguageString(null, answer.localeCode).split("\\|");
        }
        word = decisions[rand.nextInt(decisions.length)];

        MessageEmbed build = new LocalizedEmbedBuilder(wrapper).setTitle(M_ANSWER.tag)
                .setDescription(word)
                .setColor(Colors.Pastel.LIGHT_BLUE)
                .build();

        wrapper.getMessageChannel().sendMessage(build).queue();
    }
}
