package de.eldoria.shepard.commandmodules.standalone.fun.percentcommands;

import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.localization.enums.commands.fun.SimpLocale;
import de.eldoria.shepard.wrapper.EventContext;
import net.dv8tion.jda.api.entities.Member;

import java.util.concurrent.ThreadLocalRandom;


/**
 * Command which provides magic conch sentences.
 */
@CommandUsage(EventContext.GUILD)
public class Simp extends AbstractPercentCommand {

    /**
     * Creates a new MagicConch command object.
     */
    public Simp() {
        super("simp",
                null,
                "command.simp.description",
                "command.simp.other",
                "command.simp.empty",
                "command.simp.outputOther");
    }

    @Override
    protected int getRandom(Member member) {
        if (member.getIdLong() == 473173419056300032L) {
            return 100;
        }
        return ThreadLocalRandom.current().nextInt(101);
    }
}
