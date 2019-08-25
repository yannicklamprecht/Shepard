package de.chojo.shepard.contexts.reactionmessages;

import de.chojo.shepard.collections.ReactionMessageCollection;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

@Deprecated
public class ReactionMessage {
    protected String[] messageIds = new String[0];

    protected ReactionMessage() {
        ReactionMessageCollection.getInstance().addReactionMessage(this);
    }

    public String[] getMessageIds() {
        return messageIds;
    }

    public boolean execute(MessageReactionAddEvent event) {
        return false;
    }

    @Deprecated
    public String hasMessageIds(MessageReactionAddEvent event) {
        for (String id : messageIds) {
            if (event.getMessageId() == id) {
                return id;
            }
        }
        return null;
    }
}
