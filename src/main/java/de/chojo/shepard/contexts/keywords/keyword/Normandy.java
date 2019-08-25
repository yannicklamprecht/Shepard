package de.chojo.shepard.contexts.keywords.keyword;

import de.chojo.shepard.messagehandler.MessageSender;
import de.chojo.shepard.contexts.keywords.Keyword;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Normandy extends Keyword {

    /**
     * Creates a new normandy keyword object
     */
    public Normandy() {
        keywords = new String[] {"normandy"};

    }

    @Override
    public boolean execute(MessageReceivedEvent event, String key) {
        MessageSender.sendMessage("Where is my ship o.o", event.getChannel());
        return true;
    }
}
