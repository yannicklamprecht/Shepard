package de.eldoria.shepard.webapi.endpoints;

import com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import de.eldoria.shepard.webapi.BotListReporter;
import de.eldoria.shepard.webapi.apiobjects.VoteInformation;

import static spark.Spark.path;
import static spark.Spark.post;


public class BotListEnpoint {
    private BotListReporter botListReporter;

    private BotListEnpoint() {

    }

    public BotListEnpoint(BotListReporter botListReporter) {
        this.botListReporter = botListReporter;
        defineRoutes();
    }

    private void defineRoutes() {
        path("/v1", () -> {
            post("/votes", (request, response) -> {
                response.type("application/json");
                VoteInformation voteInformation = new Gson().fromJson(request.body(), VoteInformation.class);
                botListReporter.handleVote(voteInformation);

                return HttpStatusCodes.STATUS_CODE_OK;
            });
        });
    }

}
