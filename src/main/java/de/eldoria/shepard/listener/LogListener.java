package de.chojo.shepard.listener;

import de.chojo.shepard.collections.ServerCollection;
import de.chojo.shepard.messagehandler.MessageSender;
import de.chojo.shepard.ShepardBot;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        MessageSender.sendMessage("Shepard ist nun auf " + event.getGuild().getName() + " verfügbar!",
                ServerCollection.getNormandy().getTextChannelById("538094461381640192"));
    }

    @Override
    public void onReady(ReadyEvent event) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        MessageSender.sendMessage("`[" + formatter.format(date)
                        + "]` Shepard meldet sich zum Dienst! Erwarte ihre Befehle! Derzeit stehe ich auf "
                        + ShepardBot.getJDA().getGuilds().size() + " Servern zur Verfügung!",
                ServerCollection.getNormandy().getTextChannelById("538094461381640192"));
    }
}
