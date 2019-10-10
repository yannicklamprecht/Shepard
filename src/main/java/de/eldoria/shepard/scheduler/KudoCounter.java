package de.eldoria.shepard.scheduler;

import de.eldoria.shepard.database.queries.RubberPointsData;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KudoCounter implements Runnable {
    public void initialize() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this, 30, 60, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        RubberPointsData.upcountKudos();
    }
}
