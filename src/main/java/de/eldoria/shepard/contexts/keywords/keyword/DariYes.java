package de.eldoria.shepard.contexts.keywords.keyword;

import de.eldoria.shepard.contexts.keywords.Keyword;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DariYes extends Keyword {

    /**
     * Creates a new dari yes keyword.
     */
    public DariYes() {
        keywords = new String[] {"nein", "no", "oder?", "nope"};
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, String key) {
        if (event.getAuthor().getId().equalsIgnoreCase("223192558468202496")) {
            MessageSender.sendMessage("Doch " + event.getAuthor().getAsMention(), event.getChannel());
        }
    }
}
