package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.localization.enums.commands.fun.CuteLocale;
import de.eldoria.shepard.wrapper.EventContext;
import net.dv8tion.jda.api.entities.Member;

import java.util.concurrent.ThreadLocalRandom;


/**
 * Command which provides magic conch sentences.
 */
@CommandUsage(EventContext.GUILD)
public class Cute extends AbstractPercentCommand {

    /**
     * Creates a new MagicConch command object.
     */
    public Cute() {
        super("cute",
                null,
                CuteLocale.DESCRIPTION.tag,
                CuteLocale.C_OTHER.tag,
                CuteLocale.C_EMPTY.tag,
                CuteLocale.OTHER.tag);
    }

    @Override
    protected int getRandom(Member member) {
        return ThreadLocalRandom.current().nextInt(61) + 40;
    }
}
