package de.chojo.shepard.contexts.keywords.keyword;

import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.contexts.keywords.Keyword;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommanderQuestion extends Keyword {

    /**
     * Creates a new Commander Question keyword object.
     */
    public CommanderQuestion() {
        keywords = new String[] {"commander?", "shepard?"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String key) {
        if (key.equalsIgnoreCase(keywords[0])) {
            Messages.sendMessage("Commander Shepard meldet sich zum Dienst!", event.getChannel());
        }
        if (key.equalsIgnoreCase(keywords[1])) {
            Messages.sendMessage("Hier bin ich o/", event.getChannel());
        }
        return true;
    }
}
