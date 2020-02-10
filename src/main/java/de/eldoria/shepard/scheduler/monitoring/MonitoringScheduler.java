package de.eldoria.shepard.scheduler.monitoring;

import de.eldoria.shepard.database.types.Address;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class MonitoringScheduler {
    private static MonitoringScheduler instance;
    private final Map<Long, List<Address>> unreachable = new HashMap<>();

    private MonitoringScheduler() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        executor.scheduleAtFixedRate(new MonitoringCoordinator(24), 0, 5, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(new ReconnectCoordinator(), 0, 1, TimeUnit.MINUTES);
    }

    /**
     * initialize the monitoring scheduler.
     */
    public static void initialize() {
        if (instance == null) {
            instance = new MonitoringScheduler();
        }
    }

    /**
     * Get the current monitoring scheduler.
     *
     * @return instance of monitoring scheduler
     */
    public static MonitoringScheduler getInstance() {
        initialize();
        return instance;
    }


    /**
     * Get all unreachable addresses.
     *
     * @return map with guild ids and list of address objects
     */
    public Map<Long, List<Address>> getUnreachable() {
        return Collections.unmodifiableMap(unreachable);
    }

    /**
     * Marks a server as unreachable.
     *
     * @param guildId guild id for saving
     * @param address address to mark
     */
    public void markAsUnreachable(long guildId, Address address) {
        unreachable.putIfAbsent(guildId, new ArrayList<>());
        if (!unreachable.get(guildId).contains(address)) {
            unreachable.get(guildId).add(address);
        }
    }

    /**
     * Marks a address as reachable.
     *
     * @param guildId guild id
     * @param address address object to remove
     */
    void markAsReachable(long guildId, Address address) {
        if (unreachable.containsKey(guildId)) {
            unreachable.get(guildId).remove(address);
        }
    }

    /**
     * Checks if a server is already marked as unreachable.
     *
     * @param guild   guild for lookup
     * @param address address for lookup
     * @return true if the server is marked as unreachable
     */
    boolean markedAsUnreachable(long guild, Address address) {
        if (unreachable.containsKey(guild)) {
            return unreachable.get(guild).contains(address);
        }
        return false;
    }
}
