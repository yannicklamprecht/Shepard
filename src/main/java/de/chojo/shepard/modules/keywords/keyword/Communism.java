package de.chojo.shepard.modules.keywords.keyword;

import de.chojo.shepard.Messages;
import de.chojo.shepard.modules.keywords.Keyword;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Communism extends Keyword {
    public Communism(){
        keywords = new String[] {"communism", "kommunismus", "stalin"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String key) {
        Messages.sendMessage("Everything is better with a side of communism: https://www.youtube.com/watch?v=U06jlgpMtQs", event.getChannel());
        return true;
    }
}
