package de.eldoria.shepard.webapi;

import com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.collections.CommandInfos;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandInfo;
import de.eldoria.shepard.database.queries.MinecraftLinkData;
import de.eldoria.shepard.webapi.apiobjects.ApiCache;
import de.eldoria.shepard.webapi.apiobjects.CommandSearchResponse;
import de.eldoria.shepard.webapi.apiobjects.VoteInformation;
import lombok.extern.slf4j.Slf4j;
import spark.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.post;


@Slf4j
public final class ApiHandler {
    private static ApiHandler instance;

    private Map<String, ApiCache> cache = new HashMap<>();

    private BotListReporter botListReporter;

    private ApiHandler() {
        log.info("Defining Routes");
        defineRoutes();
        log.info("Routes Defined");
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

        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request
                    .headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers",
                        accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request
                    .headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods",
                        accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            if (!validateRequest(request)) {
                halt(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
            }
            logRequest(request.requestMethod() + " " + request.uri(), request);
        });


        post("/votes", (request, response) -> {
            response.type("application/json");
            VoteInformation voteInformation = new Gson().fromJson(request.body(), VoteInformation.class);
            botListReporter.handleVote(voteInformation);

            return HttpStatusCodes.STATUS_CODE_OK;
        });

        post("/minecraftlink/", (request, response) -> {
            MinecraftLinkData.addLinkCode("", "", null);
            return HttpStatusCodes.STATUS_CODE_OK;
        });

        get("/commands", (request, response) -> {
            String cacheName = "commands";

            if (cache.containsKey(cacheName) && cache.get(cacheName).isValid()) {
                return (cache.get(cacheName)).getObject();
            }

            CommandInfos commandInfos = CommandCollection.getInstance()
                    .getCommandInfos(ContextCategory.BOT_CONFIG, ContextCategory.EXCLUSIVE);
            if (cache.containsKey(cacheName)) {
                ((ApiCache<String>) cache.get(cacheName)).update(commandInfos.asJson());
            } else {
                cache.put(cacheName, new ApiCache<>(commandInfos.asJson(), 5));
            }
            return commandInfos.asJson();
        });

        get("/commandsearch/:text", (request, response) -> {
            CommandCollection instance = CommandCollection.getInstance();
            Command command = instance.getCommand(request.params(":text"));
            List<Command> similarCommands = instance.getSimilarCommands(request.params(":text"));
            return new Gson().toJson(new CommandSearchResponse(command, similarCommands));
        });

        get("/command/:text", (request, response) -> {
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
        boolean result = authorization.equals(ShepardBot.getConfig().getBotlist().getAuthorization());
        if (!result) {
			log.info("Denied access for request.{}Headers:{}{}{}Body:{}{}", lineSeparator(), lineSeparator(), request.headers().stream().map(h -> "   " + h + ": " + request.headers(h))
					.collect(Collectors.joining(lineSeparator())), lineSeparator(), lineSeparator(), request.body());
        }
        return result;
    }

    private void logRequest(String text, Request request) {
        if (ShepardBot.getConfig().debugActive()) {
			log.info("Received request on route: {}{}{}", text, lineSeparator(), request.body());
        }
    }
}
