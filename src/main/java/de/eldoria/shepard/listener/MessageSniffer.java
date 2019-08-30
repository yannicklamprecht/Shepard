package de.eldoria.shepard.listener;

import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageSniffer extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        MessageSender.logMessageAsEmbedded(event,
                Normandy.getNormandy().getTextChannelById("538087076386832431"));
    }
}
