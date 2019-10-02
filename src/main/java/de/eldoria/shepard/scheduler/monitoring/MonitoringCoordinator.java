package de.eldoria.shepard.scheduler.monitoring;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.queries.MonitoringData;
import de.eldoria.shepard.database.types.Address;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MonitoringCoordinator implements Runnable {
    private ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(10);

    @Override
    public void run() {
        AtomicInteger delay = new AtomicInteger(0);
        for (Guild guild : ShepardBot.getJDA().getGuilds()) {
            String channelId = MonitoringData.getMonitoringChannel(guild, null);
            if (channelId == null) {
                continue;
            }
            TextChannel channel = guild.getTextChannelById(channelId);
            if (channel != null) {
                List<Address> addresses = MonitoringData.getMonitoringAddressesForGuild(guild, null);
                addresses.forEach(address -> {
                    if (address.isMinecraftIp()) {
                        executor.schedule(new Analyzer(address, channel), delay.getAndAdd(5), TimeUnit.SECONDS);
                    } else {
                        executor.schedule(new Analyzer(address, channel), 0, TimeUnit.SECONDS);
                    }
                });
            }
        }
    }
}
