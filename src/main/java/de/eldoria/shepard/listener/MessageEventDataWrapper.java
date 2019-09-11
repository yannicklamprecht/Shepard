package de.eldoria.shepard.listener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;

public class MessageEventDataWrapper<T extends GenericMessageEvent> {
    private MessageUpdateEvent updateEvent;
    private MessageReceivedEvent receivedEvent;
    private boolean isUpdate = false;

    public MessageEventDataWrapper(T event) {
        if (event instanceof MessageUpdateEvent) {
            updateEvent = (MessageUpdateEvent) event;
            isUpdate = true;
        } else if (event instanceof MessageReceivedEvent) {
            receivedEvent = (MessageReceivedEvent) event;
        }
    }

    public Guild getGuild(){
        return isUpdate ? updateEvent.getGuild() : receivedEvent.getGuild();
    }

    public Member getMember(){
        return isUpdate ? updateEvent.getMember() : receivedEvent.getMember();
    }

    public User getAuthor(){
        return isUpdate ? updateEvent.getAuthor() : receivedEvent.getAuthor();
    }

    public Message getMessage(){
        return isUpdate ? updateEvent.getMessage() : receivedEvent.getMessage();
    }

    public MessageChannel getChannel(){
        return isUpdate ? updateEvent.getChannel() : receivedEvent.getChannel();
    }

    @Override
    public boolean equals(Object obj) {
        return isUpdate ? updateEvent.equals(obj) : receivedEvent.equals(obj);
    }

    public ChannelType getChannelType(){
        return isUpdate ? updateEvent.getChannelType() : receivedEvent.getChannelType();
    }

    public JDA getJDA(){
        return isUpdate ? updateEvent.getJDA() : receivedEvent.getJDA();
    }

    public String getMessageId(){
        return isUpdate ? updateEvent.getMessageId() : receivedEvent.getMessageId();
    }
    public Long getMessageIdLong(){
        return isUpdate ? updateEvent.getMessageIdLong() : receivedEvent.getMessageIdLong();
    }

    public PrivateChannel getPrivateChannel(){
        return isUpdate ? updateEvent.getPrivateChannel() : receivedEvent.getPrivateChannel();
    }

    public TextChannel getTextChannel(){
        return isUpdate ? updateEvent.getTextChannel() : receivedEvent.getTextChannel();
    }

    public boolean isFromGuild(){
        return isUpdate ? updateEvent.isFromGuild() : receivedEvent.isFromGuild();
    }
}
