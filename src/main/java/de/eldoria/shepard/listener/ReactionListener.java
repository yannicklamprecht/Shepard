package de.eldoria.shepard.listener;

import de.eldoria.shepard.collections.ReactionActionCollection;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class ReactionListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        ReactionActionCollection.getInstance().invokeReactionAction(event);
    }
}
