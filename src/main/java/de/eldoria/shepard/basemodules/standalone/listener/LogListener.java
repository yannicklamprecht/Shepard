package de.eldoria.shepard.basemodules.standalone.listener;

import de.eldoria.shepard.C;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
public class LogListener extends ListenerAdapter {
    private LocalDateTime disconnected;

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        log.info(C.STATUS, "Shepard joined guild {}({}) owned by {}.",
                event.getGuild().getName(), event.getGuild().getId(), event.getGuild().getOwner().getUser().getAsTag());
    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        log.info(C.STATUS, "Shepard left guild {}({}).",
                event.getGuild().getName(), event.getGuild().getId());
    }

    @Override
    public void onReconnect(@Nonnull ReconnectedEvent event) {
        log.info("JDA reconnected");
    }

    @Override
    public void onDisconnect(@Nonnull DisconnectEvent event) {
        disconnected = LocalDateTime.now();
        log.warn("JDA disconnected");
    }

    @Override
    public void onResume(@Nonnull ResumedEvent event) {
        long seconds = Duration.between(disconnected, LocalDateTime.now()).getSeconds();
        if (seconds > 5) {
            log.info(C.STATUS,
                    "Shepard was disconnected for {} seconds. All connections reconnected. Everything is fine.",
                    seconds);
        }
    }
}
