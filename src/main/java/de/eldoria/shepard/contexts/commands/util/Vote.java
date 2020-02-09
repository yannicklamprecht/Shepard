package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.localization.enums.commands.util.VoteLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

/**
 * Command which provides a vote link to the botlist.
 */
public class Vote extends Command {

    /**
     * Creates a new vote command object.
     */
    public Vote() {
        commandName = "vote";
        commandDesc = VoteLocale.DESCRIPTION.tag;
        category = ContextCategory.UTIL;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(messageContext)
                .setTitle(VoteLocale.M_TITLE.tag)
                .setDescription(VoteLocale.M_TEXT.tag + System.lineSeparator()
                        + "[" + VoteLocale.M_CLICK + "](https://top.gg/bot/512413049894731780/vote)")
                .setThumbnail(ShepardReactions.WINK.thumbnail);
        messageContext.getTextChannel().sendMessage(builder.build()).queue();
    }
}
