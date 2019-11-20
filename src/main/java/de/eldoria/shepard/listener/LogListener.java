package de.eldoria.shepard.listener;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.time.Duration;
import java.time.LocalDateTime;

public class LogListener extends ListenerAdapter {
    private LocalDateTime disconnected;


    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        MessageSender.sendSimpleTextBox("Shepard joined a new Guild", "Shepard is now available on Guild "
                        + event.getGuild().getName() + " owned by " + event.getGuild().getOwner().getUser().getAsTag(),
                Color.green, ShepardReactions.EXCITED, Normandy.getGeneralLogChannel());
    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        MessageSender.sendSimpleTextBox("Shepard left a Guild", "Shepard is no longer available on Guild "
                        + event.getGuild().getName(),
                Color.green, ShepardReactions.CRY, Normandy.getGeneralLogChannel());
    }

    @Override
    public void onReconnect(@Nonnull ReconnectedEvent event) {
        ShepardBot.getLogger().info("JDA reconnected");
    }

    @Override
    public void onDisconnect(@Nonnull DisconnectEvent event) {
        disconnected = LocalDateTime.now();
        ShepardBot.getLogger().error("JDA disconnected");
    }

    @Override
    public void onResume(@Nonnull ResumedEvent event) {
        long seconds = Duration.between(disconnected, LocalDateTime.now()).getSeconds();
        if (seconds > 5) {
            MessageSender.sendMessage("@here " + System.lineSeparator() + "Shepard was disconnected for "
                    + seconds + " seconds and is now back.", Normandy.getGeneralLogChannel());
        }
        ShepardBot.getLogger().info("Shepard was disconnected for" + seconds + " seconds. "
                + "All connections reconnected. Everything is fine.");
    }
}
