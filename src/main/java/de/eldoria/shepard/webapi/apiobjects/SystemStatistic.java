package de.eldoria.shepard.webapi.apiobjects;

import lombok.Getter;

import java.util.List;

@Getter
public class SystemStatistic {
    private final int shardsTotal;
    private final int commands;
    private final double ramUsed;
    private final int threads;
    private final AggregatedShards aggregatedShards;
    private final List<ShardStatistic> shardStatistics;

    public SystemStatistic(int shardsTotal, int commands, double ramUsed, int threads, List<ShardStatistic> shardStatistics) {
        this.shardsTotal = shardsTotal;
        this.commands = commands;
        this.ramUsed = ramUsed;
        this.threads = threads;
        this.shardStatistics = shardStatistics;
        aggregatedShards = new AggregatedShards(shardStatistics);
    }

    @Getter
    public static class AggregatedShards {
        private final long userCount;
        private final long guildCount;
        private final long commandsDispatched;
        private final long eventsFired;

        public AggregatedShards(List<ShardStatistic> shardStatistics) {
            userCount = shardStatistics.stream()
                    .map(ShardStatistic::getUserCount).reduce(0L, Long::sum);
            guildCount = shardStatistics.stream()
                    .map(ShardStatistic::getGuildCount).reduce(0L, Long::sum);
            commandsDispatched = shardStatistics.stream()
                    .map(ShardStatistic::getCommandsDispatched).reduce(0L, Long::sum);
            eventsFired = shardStatistics.stream()
                    .map(ShardStatistic::getEventsFired).reduce(0L, Long::sum);
        }
    }
}
