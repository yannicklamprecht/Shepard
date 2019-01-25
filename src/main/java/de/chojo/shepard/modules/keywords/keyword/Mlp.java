package de.chojo.shepard.modules.keywords.keyword;

import de.chojo.shepard.Messages;
import de.chojo.shepard.modules.keywords.Keyword;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Mlp extends Keyword {
    public Mlp(){
        keywords = new String[]{"mlp", "pony"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String key) {
        if(key.equalsIgnoreCase(keywords[0])){
            Messages.sendMessage("Friendship is Magic!", event.getChannel());
        }
        if(key.equalsIgnoreCase(keywords[1])){
            Messages.sendMessage("Someone said Pony?", event.getChannel());
        }
        return true;
    }
}
