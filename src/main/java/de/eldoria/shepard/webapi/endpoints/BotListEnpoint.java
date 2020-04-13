package de.eldoria.shepard.webapi.endpoints;

import com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import de.eldoria.shepard.webapi.BotListReporter;
import de.eldoria.shepard.webapi.apiobjects.botlists.VoteInformation;

import static spark.Spark.path;
import static spark.Spark.post;


public class BotListEnpoint {
    private BotListReporter botListReporter;

    private BotListEnpoint() {

    }

    /**
     * Create a new bot list endpoint.
     *
     * @param botListReporter the botlist reporter instance used for vote handling.
     */
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
