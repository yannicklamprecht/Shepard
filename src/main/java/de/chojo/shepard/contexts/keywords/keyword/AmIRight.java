package de.chojo.shepard.contexts.keywords.keyword;

import de.chojo.shepard.messagehandler.MessageSender;
import de.chojo.shepard.contexts.keywords.Keyword;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AmIRight extends Keyword {

    /**
     * Creates new AmIRight object.
     */
    public AmIRight() {
        keywords = new String[]{"stimmts shep", "stimmt's shep", "hab ich recht shep"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String key) {
        if (event.getAuthor().getId().equalsIgnoreCase("214347948316819456")) {
            MessageSender.sendMessage("Natürlich :3", event.getChannel());
            return true;
        }
        MessageSender.sendMessage("Nein <@!214347948316819456> hat immer recht :3", event.getChannel());
        return true;
    }
}
