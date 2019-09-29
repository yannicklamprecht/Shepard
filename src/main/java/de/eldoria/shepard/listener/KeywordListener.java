package de.eldoria.shepard.listener;

import de.eldoria.shepard.collections.KeyWordCollection;
import de.eldoria.shepard.contexts.keywords.KeywordArgs;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class KeywordListener extends ListenerAdapter {

    private KeyWordCollection keyWordCollections;

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
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
