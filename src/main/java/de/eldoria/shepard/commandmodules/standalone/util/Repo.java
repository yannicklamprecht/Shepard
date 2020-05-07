package de.eldoria.shepard.commandmodules.standalone.util;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.util.RepoLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

public class Repo extends Command implements Executable {
    public Repo() {
        super("repo",
                new String[] {"git", "source", "code"},
                "Source code of Shepard",
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(messageContext)
                .setTitle(RepoLocale.M_TITLE.tag)
                .setDescription("[" + RepoLocale.M_TAKE_A_LOOK + "](https://gitlab.com/shepardbot/ShepardBot)")
                .setColor(Colors.Pastel.ORANGE)
                .setThumbnail(ShepardReactions.WINK.thumbnail);
        messageContext.getTextChannel().sendMessage(builder.build()).queue();
    }
}
