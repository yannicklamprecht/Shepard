package de.eldoria.shepard.commandmodules.monitoring.analyzer;

import de.eldoria.shepard.commandmodules.monitoring.util.Address;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqJDA;
import net.dv8tion.jda.api.JDA;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class MonitoringScheduler implements ReqJDA, ReqInit, ReqDataSource {
    private static MonitoringScheduler instance;
    private final Map<Long, List<Address>> unreachable = new HashMap<>();
    private JDA jda;
    private DataSource source;


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

    @Override
    public void init() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        executor.scheduleAtFixedRate(new MonitoringCoordinator(this, source, jda, 24), 0, 5, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(new ReconnectCoordinator(this, source, jda), 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public void addJDA(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void addDataSource(DataSource source) {

        this.source = source;
    }
}
