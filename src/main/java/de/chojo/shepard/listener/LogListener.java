package de.chojo.shepard.listener;

import de.chojo.shepard.Collections.ServerCollection;
import de.chojo.shepard.Messages;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LogListener extends ListenerAdapter {
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Messages.sendMessage("Shepard ist nun auf " + event.getGuild().getName() + " verfügbar!", ServerCollection.getNormandy().getTextChannelById("538094461381640192"));
    }
}
