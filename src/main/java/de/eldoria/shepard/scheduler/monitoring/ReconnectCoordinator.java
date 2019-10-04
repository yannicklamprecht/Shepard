package de.eldoria.shepard.scheduler.monitoring;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.queries.MonitoringData;
import de.eldoria.shepard.database.types.Address;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ReconnectCoordinator implements Runnable {
    private final int API_REQUEST_DELAY = 5;
    private ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(10);

    @Override
    public void run() {
        System.out.println("Checking for unavailable Server.");
        AtomicInteger delay = new AtomicInteger(0);
        for (Map.Entry<Long, List<Address>> set : MonitoringScheduler.getInstance().getUnreachable().entrySet()) {
            Guild guildById = ShepardBot.getJDA().getGuildById(set.getKey());
            if (guildById == null) {
                continue;
            }
            String channelId = MonitoringData.getMonitoringChannel(guildById, null);
            if (channelId == null) {
                continue;
            }
            TextChannel channel = guildById.getTextChannelById(channelId);
            if (channel != null) {
                set.getValue().forEach(address -> {
                    if (address.isMinecraftIp()) {
                        executor.schedule(new ReconnectAnalyzer(address, channel),
                                delay.getAndAdd(API_REQUEST_DELAY), TimeUnit.SECONDS);
                    } else {
                        executor.schedule(new ReconnectAnalyzer(address, channel),
                                0, TimeUnit.SECONDS);
                    }
                });
            }
        }
    }
}
