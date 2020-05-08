package de.eldoria.shepard.webapi.endpoints;

import com.google.gson.Gson;
import de.eldoria.shepard.basemodules.commanddispatching.CommandHub;
import de.eldoria.shepard.core.Statistics;
import de.eldoria.shepard.modulebuilder.requirements.ReqCommands;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import de.eldoria.shepard.modulebuilder.requirements.ReqStatistics;
import de.eldoria.shepard.webapi.apiobjects.ShardStatistic;
import de.eldoria.shepard.webapi.apiobjects.SystemResponse;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static spark.Spark.get;
import static spark.Spark.path;

@Slf4j
public class SystemEndpoint implements ReqShardManager, ReqCommands, ReqStatistics, ReqInit {

    private static DecimalFormat df = new DecimalFormat("0.0000");
    private ShardManager shardManager;
    private CommandHub commandHub;
    private Statistics statistics;

    @Override
    public void init() {
        path("/v1", () -> {
            get("/system", ((request, response) -> {
                List<ShardStatistic> shardStatistics = shardManager.getShardCache()
                        .stream().map(jda -> {
                            try {
                                return statistics.getShardStatistic(jda);
                            } catch (ExecutionException e) {
                                log.error("An error occured while building the system statistics", e);
                            }
                            return new ShardStatistic(jda.getShardInfo().getShardId(),
                                    JDA.Status.DISCONNECTED, 0, 0, 0, 0);
                        }).collect(Collectors.toList());

                int shardsTotal = shardManager.getShardsTotal();
                int commands = commandHub.getCommands().size();
                double ramUsed = Math.round(
                        (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000d / 1000d) / 1000d;

                int threads = Thread.activeCount();

                SystemResponse sysResponse = new SystemResponse(shardsTotal, commands, ramUsed, threads, shardStatistics);

                return new Gson().toJson(sysResponse);
            }));
        });
    }

    @Override
    public void addCommands(CommandHub commandHub) {
        this.commandHub = commandHub;
    }

    @Override
    public void addShardManager(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    public void addStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}