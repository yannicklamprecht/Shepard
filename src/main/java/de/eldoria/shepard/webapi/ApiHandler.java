package de.eldoria.shepard.webapi;

import com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.collections.CommandInfos;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandInfo;
import de.eldoria.shepard.database.queries.MinecraftLinkData;
import de.eldoria.shepard.webapi.apiobjects.ApiCache;
import de.eldoria.shepard.webapi.apiobjects.CommandSearchResponse;
import de.eldoria.shepard.webapi.apiobjects.VoteInformation;
import spark.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

public final class ApiHandler {
    private static ApiHandler instance;

    private Map<String, ApiCache> cache = new HashMap<>();

    private BotListReporter botListReporter;

    private ApiHandler() {
        ShepardBot.getLogger().info("Defining Routes");
        defineRoutes();
        ShepardBot.getLogger().info("Routes Defined");
        botListReporter = BotListReporter.initialize();
    }

    /**
     * Get the ApiHandler.
     *
     * @return api handler instance
     */
    public static ApiHandler getInstance() {
        if (instance == null) {
            instance = new ApiHandler();
        }
        return instance;
    }

    private void defineRoutes() {
        port(34555);

        post("/votes", (request, response) -> {
            if (!validateRequest(request)) {
                response.status(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
                return HttpStatusCodes.STATUS_CODE_UNAUTHORIZED;
            }

            logRequest("Votes", request);

            response.type("application/json");
            VoteInformation voteInformation = new Gson().fromJson(request.body(), VoteInformation.class);
            botListReporter.handleVote(voteInformation);

            return HttpStatusCodes.STATUS_CODE_OK;
        });

        post("/minecraftlink/", (request, response) -> {
            if (!validateRequest(request)) {
                response.status(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
                return HttpStatusCodes.STATUS_CODE_UNAUTHORIZED;
            }

            logRequest("/votes", request);

            MinecraftLinkData.addLinkCode("", "", null);
            return HttpStatusCodes.STATUS_CODE_OK;
        });

        get("/commands", (request, response) -> {
            String cacheName = "commands";
            if (!validateRequest(request)) {
                response.status(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
                return HttpStatusCodes.STATUS_CODE_UNAUTHORIZED;
            }
            if (cache.containsKey(cacheName) && cache.get(cacheName).isValid()) {
                return (cache.get(cacheName)).getObject();
            }

            CommandInfos commandInfos = CommandCollection.getInstance().getCommandInfos();
            if (cache.containsKey(cacheName)) {
                ((ApiCache<String>) cache.get(cacheName)).update(commandInfos.asJson());
            } else {
                cache.put(cacheName, new ApiCache<>(commandInfos.asJson(), 5));
            }
            return commandInfos.asJson();
        });

        get("/commandsearch/:text", (request, response) -> {
            if (!validateRequest(request)) {
                response.status(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
                return HttpStatusCodes.STATUS_CODE_UNAUTHORIZED;
            }
            logRequest("/commandsearch/:text", request);

            CommandCollection instance = CommandCollection.getInstance();
            Command command = instance.getCommand(request.params(":text"));
            List<Command> similarCommands = instance.getSimilarCommands(request.params(":text"));
            return new Gson().toJson(new CommandSearchResponse(command, similarCommands));
        });

        get("/command/:text", (request, response) -> {
            if (!validateRequest(request)) {
                response.status(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
                return HttpStatusCodes.STATUS_CODE_UNAUTHORIZED;
            }
            logRequest("/command/:text", request);

            CommandCollection instance = CommandCollection.getInstance();
            Command command = instance.getCommand(request.params(":text"));
            return new Gson().toJson(new CommandInfo(command));
        });

    }

    /**
     * Validates if a request has the right header.
     *
     * @param request request to check
     * @return true if the header is correct.
     */
    private boolean validateRequest(Request request) {
        String authorization = request.headers("Authorization");
        return authorization.equals(ShepardBot.getConfig().getBotlist().getAuthorization());
    }

    private void logRequest(String text, Request request) {
        if (ShepardBot.getConfig().debugActive()) {
            ShepardBot.getLogger().info(text + System.lineSeparator() + request.body());
        }
    }
}