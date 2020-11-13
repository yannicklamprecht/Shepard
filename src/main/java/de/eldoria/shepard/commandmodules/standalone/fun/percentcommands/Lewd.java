package de.eldoria.shepard.commandmodules.standalone.fun.percentcommands;

import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.localization.enums.commands.fun.LewdLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.concurrent.ThreadLocalRandom;


/**
 * Command which provides magic conch sentences.
 */
@CommandUsage(EventContext.GUILD)
public class Lewd extends AbstractPercentCommand {

    /**
     * Creates a new MagicConch command object.
     */
    public Lewd() {
        super("lewd",
                new String[] {"pure"},
                "command.pure.description",
                "command.pure.other",
                "command.pure.empty",
                null);
    }

    @Override
    protected int getRandom(Member member) {
        if (member.getUser().getIdLong() == 295377713009524746L) {
            return ThreadLocalRandom.current().nextInt(50);
        }
        return ThreadLocalRandom.current().nextInt(101);
    }

    @Override
    protected MessageEmbed getEmbed(EventWrapper event, String label, Member member, int value) {
        if ("lewd".equalsIgnoreCase(label)) {
            return new LocalizedEmbedBuilder(event)
                    .setDescription(
                            TextLocalizer.localizeAllAndReplace(LewdLocale.LEWD.tag, event,
                                    "**" + value + "**", "**" + member.getEffectiveName() + "**"))
                    .setColor(Colors.Pastel.ORANGE)
                    .build();
        } else {
            return new LocalizedEmbedBuilder(event)
                    .setDescription(
                            TextLocalizer.localizeAllAndReplace(LewdLocale.PURE.tag, event,
                                    "**" + (100 - value) + "**", "**" + member.getEffectiveName() + "**"))
                    .setColor(Colors.Pastel.ORANGE)
                    .build();
        }
    }
}
