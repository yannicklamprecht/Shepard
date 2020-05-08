package de.eldoria.shepard.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import de.eldoria.shepard.webapi.apiobjects.ShardStatistic;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Statistics implements Runnable, ReqShardManager, ReqInit {
    private final Map<Integer, long[]> commandsDispatched = new HashMap<>();
    private final Map<Integer, long[]> eventsFired = new HashMap<>();
    private Cache<Integer, Integer> userCache;
    private Cache<Integer, Integer> guildCache;
    private int currentMin = 0;
    private ShardManager shardManager;

    public Statistics() {
    }

    private int minute() {
        return LocalTime.now().getMinute();
    }

    public void commandDispatched(JDA shard) {
        int shardId = shard.getShardInfo().getShardId();
        commandsDispatched.get(shardId)[currentMin]++;
    }

    public void eventDispatched(JDA shard) {
        int shardId = shard.getShardInfo().getShardId();
        eventsFired.get(shardId)[currentMin]++;
    }

    @Override
    public void run() {
        currentMin = minute();
        resetMinute(commandsDispatched);
        resetMinute(eventsFired);
    }

    private void resetMinute(Map<Integer, long[]> map) {
        for (int shardId = 0; shardId < shardManager.getShardsTotal(); shardId++) {
            map.putIfAbsent(shardId, new long[60]);
            map.get(shardId)[currentMin] = 0;
        }
    }

    public ShardStatistic getShardStatistic(JDA jda) throws ExecutionException {

        int shardId = jda.getShardInfo().getShardId();
        long commandsDispatched = arraySum(this.commandsDispatched.get(shardId));
        long eventsFired = arraySum(this.eventsFired.get(shardId));

        return new ShardStatistic(shardId + 1,
                jda.getStatus(),
                userCache.get(shardId, () -> jda.getUsers().size()),
                guildCache.get(shardId, () -> jda.getGuilds().size()),
                commandsDispatched,
                eventsFired);
    }

    private long arraySum(long[] array) {
        return Arrays.stream(array).sum();
    }

    @Override
    public void addShardManager(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    public void init() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this, 0, 60, TimeUnit.SECONDS);

        userCache = CacheBuilder.newBuilder()
                .maximumSize(shardManager.getShardsTotal())
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();

        guildCache = CacheBuilder.newBuilder()
                .maximumSize(shardManager.getShardsTotal())
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }
}
