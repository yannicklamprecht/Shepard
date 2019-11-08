package de.eldoria.shepard.contexts.keywords.keyword;

import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.keywords.Keyword;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AmIRight extends Keyword {

    /**
     * Creates new AmIRight object.
     */
    public AmIRight() {
        keywords = new String[] {"stimmts shep", "stimmt's shep", "hab ich recht shep"};
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, String key) {
        if (event.getAuthor().getId().equals("214347948316819456")) {
            MessageSender.sendMessage("Natürlich :3", event.getChannel());
            return;
        }
        MessageSender.sendMessage("Nein <@!214347948316819456> hat immer recht :3", event.getChannel());
    }
}
