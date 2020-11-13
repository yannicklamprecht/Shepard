package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.fun.WiseFoxLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Random;


/**
 * Command which provides magic conch sentences.
 */
public class WiseFox extends Command implements Executable {
    /**
     * Creates a new MagicConch command object.
     */
    public WiseFox() {
        super("wiseFox",
                new String[] {"omniscientFox", "foxadvisor", "askFox"},
                "command.wiseFox.description",
                SubCommand.builder("wiseFox")
                        .addSubcommand(null,
                                Parameter.createInput("command.general.argument.message", "command.general.argument.message", false))
                        .build(),
                "command.wiseFox.description",
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String word;
        Random rand = new Random();
        int type = rand.nextInt(3);


        WiseFoxLocale answer;
        switch (type) {
            case 0:
                answer = WiseFoxLocale.ANSWER_POSITIVE;
                break;
            case 1:
                answer = WiseFoxLocale.ANSWER_NEGATIVE;
                break;
            case 2:
                answer = WiseFoxLocale.ANSWER_NEUTRAL;
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

        MessageEmbed build = new LocalizedEmbedBuilder(wrapper).setTitle(WiseFoxLocale.M_ANSWER.tag)
                .setDescription(word)
                .setColor(Colors.Pastel.ORANGE)
                .setThumbnail("https://chojos.lewds.de/Darkslategrey_GoldeneyeWhooper_is_Shoddy.png")
                .build();
        wrapper.getMessageChannel().sendMessage(build).queue();
    }
}
