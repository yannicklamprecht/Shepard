package de.chojo.shepard.listener;

import de.chojo.shepard.collections.KeyWordCollection;
import de.chojo.shepard.contexts.keywords.KeywordArgs;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class KeyWordListener extends ListenerAdapter {

    private KeyWordCollection keyWordCollections;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (keyWordCollections == null) {
            keyWordCollections = KeyWordCollection.getInstance();
        }

        KeywordArgs kwa = keyWordCollections.getKeyword(event);

        if (kwa != null) {

            kwa.getKeyword().execute(event, kwa.getKey());
        }

    }
}
