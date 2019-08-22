package de.chojo.shepard.contexts.keywords.keyword;

import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.contexts.keywords.Keyword;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DariNope extends Keyword {

    public DariNope() {
        keywords = new String[]{"richtig?", "oder?", "ja", "doch"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String key) {
        if (event.getAuthor().getId().equalsIgnoreCase("223192558468202496")) {
            Messages.sendMessage("Nein " + event.getAuthor().getAsMention(), event.getChannel());

        }
        return true;
    }

}
