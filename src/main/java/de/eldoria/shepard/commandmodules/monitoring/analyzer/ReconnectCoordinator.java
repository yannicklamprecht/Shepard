package de.eldoria.shepard.commandmodules.monitoring.analyzer;

import de.eldoria.shepard.commandmodules.monitoring.data.MonitoringData;
import de.eldoria.shepard.commandmodules.monitoring.util.Address;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class ReconnectCoordinator implements Runnable {
    private static final int API_REQUEST_DELAY = 5;
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(10);
    private final MonitoringScheduler scheduler;
    private final MonitoringData monitoringData;
    private final JDA jda;


    /**
     * Create a new reconnect coordinator.
     *
     * @param scheduler scheduler for managing connection tests
     * @param source    data source for information retrieval
     * @param jda       jda instance
     */
    ReconnectCoordinator(MonitoringScheduler scheduler, DataSource source, JDA jda) {
        this.scheduler = scheduler;
        this.jda = jda;
        monitoringData = new MonitoringData(source);
    }


    @Override
    public void run() {
        if (scheduler.getUnreachable().isEmpty()) {
            return;
        }

        log.debug("Checking for unavailable Server.");
        AtomicInteger delay = new AtomicInteger(0);
        for (Map.Entry<Long, List<Address>> set : scheduler.getUnreachable().entrySet()) {
            Guild guildById = jda.getGuildById(set.getKey());
            if (guildById == null) {
                continue;
            }
            String channelId = monitoringData.getMonitoringChannel(guildById, null);
            if (channelId == null) {
                continue;
            }
            TextChannel channel = guildById.getTextChannelById(channelId);
            if (channel != null) {
                set.getValue().forEach(address -> {
                    if (address.isMinecraftIp()) {
                        executor.schedule(new ReconnectAnalyzer(scheduler, address, channel),
                                delay.getAndAdd(API_REQUEST_DELAY), TimeUnit.SECONDS);
                    } else {
                        executor.schedule(new ReconnectAnalyzer(scheduler, address, channel),
                                0, TimeUnit.SECONDS);
                    }
                });
            }
        }
    }
}
