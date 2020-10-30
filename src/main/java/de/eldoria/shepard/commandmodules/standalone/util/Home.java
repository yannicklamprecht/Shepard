package de.eldoria.shepard.commandmodules.standalone.util;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.wrapper.EventWrapper;

import static de.eldoria.shepard.localization.enums.commands.util.HomeLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.util.HomeLocale.M_COME_ON_BOARD;
import static de.eldoria.shepard.localization.enums.commands.util.HomeLocale.M_JOIN_NOW;

/**
 * Command which provides a invite link to normandy.
 */
public class Home extends Command implements Executable {
    /**
     * Creates a new home command object.
     */
    public Home() {
        super("home",
                new String[] {"normandy", "support"},
                DESCRIPTION.tag,
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(wrapper).setTitle(M_COME_ON_BOARD.tag).setDescription("[" + M_JOIN_NOW + "](https://discord.gg/AJyFGAj)")
                .setColor(Colors.Pastel.DARK_RED).setThumbnail(ShepardReactions.CAT.thumbnail);

        wrapper.getMessageChannel().sendMessage(builder.build()).queue();
    }
}
