package de.eldoria.shepard.contexts.keywords.keyword;

import de.eldoria.shepard.contexts.keywords.Keyword;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Normandy extends Keyword {

    /**
     * Creates a new normandy keyword object.
     */
    public Normandy() {
        keywords = new String[] {"normandy"};
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, String key) {
        MessageSender.sendMessage("Where is my ship o.o", event.getChannel());
    }
}
