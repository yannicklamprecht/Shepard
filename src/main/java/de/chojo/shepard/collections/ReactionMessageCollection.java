package de.chojo.shepard.collections;

import de.chojo.shepard.contexts.reactionMessages.ReactionMessage;

import java.util.ArrayList;

public class ReactionMessageCollection {
    private static ReactionMessageCollection instance;

    private final ArrayList<ReactionMessage> messages = new ArrayList<>();

    public static ReactionMessageCollection getInstance() {
        if (instance == null) {
            synchronized (ReactionMessageCollection.class) {
                if (instance == null) {
                    instance = new ReactionMessageCollection();
                }
            }
        }
        return instance;
    }

    private ReactionMessageCollection(){}

    public void addReactionMessage(ReactionMessage message) { messages.add(message); }

    public ReactionMessage get(String messageId) {
        for (ReactionMessage message : messages) {
            for (String id : message.getMessageIds()) {
                if (id == messageId) {
                    return message;
                }
            }
        }
        return null;
    }
}
