package de.eldoria.shepard.contexts.keywords.keyword;

import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.keywords.Keyword;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Communism extends Keyword {

    /**
     * Creates a new communism keyword object.
     */
    public Communism() {
        keywords = new String[] {"communism", "kommunismus", "stalin"};
    }

    @Override
    public void execute(MessageReceivedEvent event, String key) {
        MessageSender.sendMessage("Everything is better with a side of communism: https://www.youtube.com/watch?v=U06jlgpMtQs", event.getChannel());
    }
}
