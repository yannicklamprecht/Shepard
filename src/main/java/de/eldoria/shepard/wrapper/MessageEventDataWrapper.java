package de.eldoria.shepard.wrapper;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;

public class MessageEventDataWrapper<T extends GenericMessageEvent> extends GenericMessageEvent {
    private MessageUpdateEvent updateEvent;
    private MessageReceivedEvent receivedEvent;
    private boolean isUpdate = false;

    public MessageEventDataWrapper(T event) {
        super(event.getJDA(), event.getResponseNumber(), event.getMessageIdLong(), event.getChannel());

        if (event instanceof MessageUpdateEvent) {
            updateEvent = (MessageUpdateEvent) event;
            isUpdate = true;
        } else if (event instanceof MessageReceivedEvent) {
            receivedEvent = (MessageReceivedEvent) event;
        }
    }

    public Member getMember() {
        return isUpdate ? updateEvent.getMember() : receivedEvent.getMember();
    }

    public User getAuthor() {
        return isUpdate ? updateEvent.getAuthor() : receivedEvent.getAuthor();
    }

    public Message getMessage() {
        return isUpdate ? updateEvent.getMessage() : receivedEvent.getMessage();
    }

    @Override
    public boolean equals(Object obj) {
        return isUpdate ? updateEvent.equals(obj) : receivedEvent.equals(obj);
    }
}
