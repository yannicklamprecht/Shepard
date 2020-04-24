package de.eldoria.shepard.commandmodules.kudos.scheduler;

import de.eldoria.shepard.commandmodules.kudos.data.KudoData;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;

import javax.sql.DataSource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KudoCounter implements Runnable, ReqDataSource, ReqConfig, ReqInit {


    private Config config;
    private KudoData kudoData;

    @Override
    public void run() {
        kudoData.upcountKudos();
    }

    @Override
    public void addDataSource(DataSource source) {
        kudoData = new KudoData(source);
    }


    @Override
    public void init() {
        if (config.isBeta()) return;
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this, 30, 60, TimeUnit.MINUTES);
    }

    @Override
    public void addConfig(Config config) {

        this.config = config;
    }
}
