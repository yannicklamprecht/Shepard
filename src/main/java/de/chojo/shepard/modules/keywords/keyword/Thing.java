package de.chojo.shepard.modules.keywords.keyword;

import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.ShepardBot;
import de.chojo.shepard.modules.keywords.Keyword;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Thing extends Keyword {

    public Thing() {
        keywords = new String[]{"ding"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String key) {
        Messages.sendMessage(event.getMessage().getContentRaw() + " by <@" + event.getAuthor().getId() + ">", ShepardBot.getJDA().getGuildById("336473392863510538").getTextChannelById("538103926126280706"));
        return true;
    }
}
