package de.eldoria.shepard.wrapper;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;

public class MessageEventDataWrapper extends GenericMessageEvent {
    private Member member;
    private User author;
    private Message message;

    public MessageEventDataWrapper(GenericGuildMessageEvent event) {
        super(event.getJDA(), event.getResponseNumber(), event.getMessageIdLong(), event.getChannel());

        GuildMessageUpdateEvent updateEvent = null;
        GuildMessageReceivedEvent receivedEvent = null;
        boolean isUpdate = false;
        if (event instanceof GuildMessageUpdateEvent) {
            updateEvent = (GuildMessageUpdateEvent) event;
            isUpdate = true;
        } else if (event instanceof GuildMessageReceivedEvent) {
            receivedEvent = (GuildMessageReceivedEvent) event;
        } else {
            return;
        }

        member = isUpdate ? updateEvent.getMember() : receivedEvent.getMember();
        author = isUpdate ? updateEvent.getAuthor() : receivedEvent.getAuthor();
        message = isUpdate ? updateEvent.getMessage() : receivedEvent.getMessage();
    }

    public Member getMember() {
        return member;
    }

    public User getAuthor() {
        return author;
    }

    public Message getMessage() {
        return message;
    }
}
