package de.chojo.shepard.listener;

import de.chojo.shepard.collections.ReactionMessageCollection;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReactionListener extends ListenerAdapter {

    private ReactionMessageCollection reactionMessageCollection;

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        if (reactionMessageCollection == null) {
            reactionMessageCollection = ReactionMessageCollection.getInstance();
        }



        if (reactionMessageCollection.get(e.getMessageId()) != null) {
            reactionMessageCollection.get(e.getMessageId()).execute(e);
        }
    }
}
