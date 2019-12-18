package de.eldoria.shepard.contexts.keywords.keyword;

import de.eldoria.shepard.contexts.keywords.Keyword;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DariNope extends Keyword {

    /**
     * Creates a new dari nope keyword object.
     */
    public DariNope() {
        keywords = new String[] {"richtig?", "oder?", "ja", "doch"};
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, String key) {
        if (event.getAuthor().getId().equalsIgnoreCase("223192558468202496")) {
            MessageSender.sendMessage("Nein " + event.getAuthor().getAsMention(), event.getChannel());
        }
    }
}
