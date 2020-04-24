package de.eldoria.shepard.commandmodules.monitoring.analyzer;

import de.eldoria.shepard.commandmodules.monitoring.data.MonitoringData;
import de.eldoria.shepard.commandmodules.monitoring.util.Address;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MonitoringCoordinator implements Runnable {
    private static final int API_REQUEST_DELAY = 5;
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(10);
    private final int broadcastCount;
    private final MonitoringScheduler scheduler;
    private final JDA jda;
    private final MonitoringData monitoringData;
    private int counts;

    /**
     * Initiates a monitoring coordinator with broadcast settings.
     *
     * @param broadcastCount the x. message which should be printed.
     * @param jda  jda instance
     * @param scheduler  scheduler for monitoring checks
     * @param source data source for monitoring
     */
    MonitoringCoordinator(MonitoringScheduler scheduler, DataSource source, JDA jda, int broadcastCount) {
        this.scheduler = scheduler;
        this.jda = jda;
        this.broadcastCount = broadcastCount;
        monitoringData = new MonitoringData(source);
    }

    @Override
    public void run() {
        AtomicInteger delay = new AtomicInteger(0);
        for (Guild guild : jda.getGuilds()) {
            String channelId = monitoringData.getMonitoringChannel(guild, null);
            if (channelId == null) {
                continue;
            }
            TextChannel channel = guild.getTextChannelById(channelId);
            if (channel != null) {
                List<Address> addresses = monitoringData.getMonitoringAddressesForGuild(guild, null);
                addresses.forEach(address -> {
                    if (address.isMinecraftIp()) {
                        executor.schedule(new Analyzer(scheduler, address, channel, onlyError()),
                                delay.getAndAdd(API_REQUEST_DELAY), TimeUnit.SECONDS);
                    } else {
                        executor.schedule(new Analyzer(scheduler, address, channel, onlyError()),
                                0, TimeUnit.SECONDS);
                    }
                });
            }
        }
        counts++;
        counts = counts % broadcastCount;
    }


    private boolean onlyError() {
        return counts != 0;
    }
}
