package de.eldoria.shepard.contexts.keywords.keyword;

import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.keywords.Keyword;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Mlp extends Keyword {

    /**
     * Create new MLP keyword object.
     */
    public Mlp() {
        keywords = new String[] {"mlp", "pony"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String key) {
        if (key.equalsIgnoreCase(keywords[0])) {
            MessageSender.sendMessage("Friendship is Magic!", event.getChannel());
        }
        if (key.equalsIgnoreCase(keywords[1])) {
            MessageSender.sendMessage("Someone said Pony?", event.getChannel());
        }
        return true;
    }
}
