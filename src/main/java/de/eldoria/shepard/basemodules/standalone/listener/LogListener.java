package de.eldoria.shepard.basemodules.standalone.listener;

import de.eldoria.shepard.C;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class LogListener extends ListenerAdapter implements Runnable {
    private final Map<Integer, Instant> disconnected = new HashMap<>();

    public LogListener() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this, 60, 60, TimeUnit.SECONDS);
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        log.info(C.STATUS, "Shepard joined guild {}({}) owned by {} on shard {}.",
                event.getGuild().getName(),
                event.getGuild().getId(),
                event.getGuild().getOwner().getUser().getAsTag(),
                event.getJDA().getShardInfo().getShardId());
    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        log.info(C.STATUS, "Shepard left guild {}({}) on shard {}.",
                event.getGuild().getName(),
                event.getGuild().getId(),
                event.getJDA().getShardInfo().getShardId());
    }

    @Override
    public void onDisconnect(@Nonnull DisconnectEvent event) {
        disconnected.put(event.getJDA().getShardInfo().getShardId(), Instant.now());
        log.debug("Shard {} disconnected", event.getJDA().getShardInfo().getShardId());
    }

    @Override
    public void onReconnect(@Nonnull ReconnectedEvent event) {
        handleShardReconnect(event.getJDA());
    }

    @Override
    public void onResume(@Nonnull ResumedEvent event) {
        handleShardReconnect(event.getJDA());
    }

    private void handleShardReconnect(JDA jda) {
        int shardId = jda.getShardInfo().getShardId();
        long seconds = Duration.between(
                disconnected.getOrDefault(shardId, Instant.now()), Instant.now())
                .getSeconds();
        disconnected.remove(shardId);
        if (seconds > 5) {
            log.info(C.STATUS,
                    "Shard {} was disconnected for {} seconds. Everything is fine.",
                    shardId, seconds);
        } else {
            log.debug("Shard {} reconnected", jda.getShardInfo().getShardId());
        }
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        handleShardReconnect(event.getJDA());
        log.info("Shard {}/{} started. Shard is connected to {} guilds.",
                event.getJDA().getShardInfo().getShardId() + 1,
                event.getJDA().getShardManager().getShardsTotal(),
                event.getGuildTotalCount());
    }

    @Override
    public void run() {
        if (disconnected.isEmpty()) return;

        String message = disconnected.entrySet().stream().map(e -> {
            long seconds = Duration.between(e.getValue(), Instant.now()).getSeconds();
            return String.format(" Shard %d is disconnected since %d seconds", e.getKey(), seconds);
        }).collect(Collectors.joining("\n"));

        log.warn(C.NOTIFY_ADMIN, message);
    }
}
