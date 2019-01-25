package de.chojo.shepard.modules.keywords.keyword;

import de.chojo.shepard.Messages;
import de.chojo.shepard.modules.keywords.Keyword;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Normandy extends Keyword {
    public Normandy(){
        keywords = new String[] {"normandy"};

    }

    @Override
    public boolean execute(MessageReceivedEvent event, String key) {
        Messages.sendMessage("Where is my ship o.o", event.getChannel());
        return true;
    }
}
