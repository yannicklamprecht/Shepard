package de.eldoria.shepard.commandmodules.standalone.fun.percentcommands;

import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.localization.enums.commands.fun.WaifuLocale;
import de.eldoria.shepard.wrapper.EventContext;
import net.dv8tion.jda.api.entities.Member;

import java.util.concurrent.ThreadLocalRandom;


/**
 * Command which provides magic conch sentences.
 */
@CommandUsage(EventContext.GUILD)
public class Waifu extends AbstractPercentCommand {

    /**
     * Creates a new MagicConch command object.
     */
    public Waifu() {
        super("waifu",
                null,
                "command.waifu.description",
                "command.waifu.other",
                "command.waifu.empty",
                "command.waifu.outputOther");
    }

    @Override
    protected int getRandom(Member member) {
        return ThreadLocalRandom.current().nextInt(61) + 30;
    }
}
