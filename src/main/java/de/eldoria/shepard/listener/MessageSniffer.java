package de.eldoria.shepard.listener;

import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class MessageSniffer extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        MessageSender.logMessageAsEmbedded(new MessageEventDataWrapper(event),
                Normandy.getNormandy().getTextChannelById("538087076386832431"));
    }
}
