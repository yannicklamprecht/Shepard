package de.eldoria.shepard.webapi.endpoints;

import com.google.gson.Gson;
import de.eldoria.shepard.core.Statistics;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqStatistics;
import lombok.extern.slf4j.Slf4j;

import static spark.Spark.get;
import static spark.Spark.path;

@Slf4j
public class SystemEndpoint implements ReqStatistics, ReqInit {

    private Statistics statistics;

    @Override
    public void init() {
        path("/v1", () -> {
            get("/system", ((request, response) -> new Gson().toJson(statistics.getSystemStatistic())));
        });
    }

    @Override
    public void addStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}