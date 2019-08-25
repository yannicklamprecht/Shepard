package de.chojo.shepard.contexts.keywords.keyword;

import de.chojo.shepard.messagehandler.MessageSender;
import de.chojo.shepard.contexts.keywords.Keyword;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Communism extends Keyword {

    /**
     * Creates a new communism keyword object.
     */
    public Communism() {
        keywords = new String[] {"communism", "kommunismus", "stalin"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String key) {
        MessageSender.sendMessage("Everything is better with a side of communism: https://www.youtube.com/watch?v=U06jlgpMtQs", event.getChannel());
        return true;
    }
}
