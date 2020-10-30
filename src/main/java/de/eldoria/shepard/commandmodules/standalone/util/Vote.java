package de.eldoria.shepard.commandmodules.standalone.util;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.util.VoteLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.wrapper.EventWrapper;

/**
 * Command which provides a vote link to the botlist.
 */
public class Vote extends Command implements Executable {

    /**
     * Creates a new vote command object.
     */
    public Vote() {
        super("vote",
                null,
                VoteLocale.DESCRIPTION.tag,
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(wrapper)
                .setTitle(VoteLocale.M_TITLE.tag)
                .setDescription(VoteLocale.M_TEXT.tag + System.lineSeparator()
                        + "[" + VoteLocale.M_CLICK + "(top.gg)](https://top.gg/bot/512413049894731780/vote)\n"
                        + "[" + VoteLocale.M_CLICK + "(discordbotlist.com)](https://discordbotlist.com/bots/512413049894731780)")
                .setThumbnail(ShepardReactions.WINK.thumbnail);
        wrapper.getMessageChannel().sendMessage(builder.build()).queue();
    }
}
