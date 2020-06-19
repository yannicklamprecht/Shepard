package de.eldoria.shepard.commandmodules.ban.schedular;

import de.eldoria.shepard.commandmodules.ban.data.BanData;
import de.eldoria.shepard.commandmodules.ban.types.BanDataType;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BanScheduler implements Runnable, ReqDataSource, ReqInit, ReqShardManager {
    private BanData dataSource;
    private ShardManager manager;

    @Override
    public void run() {

        List<BanDataType> returnData = dataSource.getExpiredBans(null);

        for (BanDataType bdt : returnData){
            Guild guild = manager.getGuildById(bdt.getGuild_id());
            if(guild == null) continue;
            guild.unban(bdt.getUser_id()).queue();
        }
        
    }

    @Override
    public void addDataSource(DataSource source) {
        this.dataSource = new BanData(source);
    }

    @Override
    public void init() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this, 10, 20, TimeUnit.SECONDS);
    }

    @Override
    public void addShardManager(ShardManager shardManager) {
        this.manager = shardManager;
    }
}
