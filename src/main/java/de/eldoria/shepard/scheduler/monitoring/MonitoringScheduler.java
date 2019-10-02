package de.eldoria.shepard.scheduler.monitoring;

import de.eldoria.shepard.database.types.Address;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class MonitoringScheduler {
    private static MonitoringScheduler instance;
    private Map<Long, List<Address>> unreachable = new HashMap<>();

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    private MonitoringScheduler() {
        executor.scheduleAtFixedRate(new MonitoringCoordinator(), 0, 10, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(new ReconnectCoordinator(), 0, 1, TimeUnit.MINUTES);
    }

    public static void initialize() {
        if (instance == null) {
            instance = new MonitoringScheduler();
        }
    }

    public static MonitoringScheduler getInstance() {
        initialize();
        return instance;
    }

    public Map<Long, List<Address>> getUnreachable() {
        return Collections.unmodifiableMap(unreachable);
    }

    public void markAsUnreachable(long guildId, Address address) {
        unreachable.putIfAbsent(guildId, new ArrayList<>());
        if (!unreachable.get(guildId).contains(address)) {
            unreachable.get(guildId).add(address);
        }
    }

    public void markAsReachable(long guildId, Address address) {
        if (unreachable.containsKey(guildId)) {
            unreachable.get(guildId).remove(address);
        }
    }

    public boolean markedAsUnreachable(long guild, Address address) {
        if (unreachable.containsKey(guild)) {
            return unreachable.get(guild).contains(address);
        }
        return false;
    }
}
