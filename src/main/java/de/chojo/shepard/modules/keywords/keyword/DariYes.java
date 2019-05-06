package de.chojo.shepard.modules.keywords.keyword;

import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.modules.keywords.Keyword;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DariYes extends Keyword {

    public DariYes() {
        keywords = new String[]{"nein", "no", "oder?", "nope"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String key) {
        if (event.getAuthor().getId().equalsIgnoreCase("223192558468202496")) {
            Messages.sendMessage("Doch " + event.getAuthor().getAsMention(), event.getChannel());

        }
        return true;
    }

}
