package de.eldoria.shepard.webapi.endpoints;

import com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.webapi.apiobjects.botlists.votes.DiscordBotListVote;
import de.eldoria.shepard.webapi.apiobjects.botlists.votes.TopGgVote;
import de.eldoria.shepard.webapi.apiobjects.botlists.votes.VoteWrapper;
import de.eldoria.shepard.webapi.botlistreporter.BotListReporter;
import lombok.extern.slf4j.Slf4j;

import java.util.OptionalLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static spark.Spark.path;
import static spark.Spark.post;

@Slf4j
public class BotListEndpoint {
    private final BotListReporter botListReporter;

    /**
     * Create a new bot list endpoint.
     *
     * @param botListReporter the botlist reporter instance used for vote handling.
     */
    public BotListEndpoint(BotListReporter botListReporter) {
        this.botListReporter = botListReporter;
        defineRoutes();
    }

    private void defineRoutes() {
        path("/v1", () -> path("/votes", () -> {
            // TODO: Add endpoint for bots.ondiscord.xyz and add claim command to claim kudos daily
            post("/topgg", (request, response) -> {
                response.type("application/json");
                TopGgVote vote = new Gson().fromJson(request.body(), TopGgVote.class);
                botListReporter.handleVote(new VoteWrapper(vote));

                return HttpStatusCodes.STATUS_CODE_OK;
            });

            post("/discordbotlistcom", (request, response) -> {
                response.type("application/json");
                DiscordBotListVote vote = new Gson().fromJson(request.body(), DiscordBotListVote.class);
                botListReporter.handleVote(new VoteWrapper(vote));

                return HttpStatusCodes.STATUS_CODE_OK;
            });
        }));
    }
}
