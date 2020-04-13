package de.eldoria.shepard.webapi;

import com.google.api.client.http.HttpStatusCodes;
import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.webapi.apiobjects.ApiCache;
import de.eldoria.shepard.webapi.endpoints.BotListEndpoint;
import de.eldoria.shepard.webapi.endpoints.CommandEndpoint;
import de.eldoria.shepard.webapi.endpoints.KudosEndpoint;
import de.eldoria.shepard.webapi.endpoints.MinecraftLinkEndpoint;
import lombok.extern.slf4j.Slf4j;
import spark.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static spark.Spark.before;
import static spark.Spark.halt;
import static spark.Spark.options;
import static spark.Spark.port;


@Slf4j
public final class ApiHandler {
    private static ApiHandler instance;

    private Map<String, ApiCache> cache = new HashMap<>();


    private ApiHandler() {
        log.info("Initializing api");
        initializeApi();
        log.info("API initialized. Defining Routes");
        defineRoutes();
        log.info("Routes Defined. API setup completed!");
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
        new BotListEndpoint(BotListReporter.initialize());
        new CommandEndpoint();
        new MinecraftLinkEndpoint();
        new KudosEndpoint();
    }

    private void initializeApi() {
        port(ShepardBot.getConfig().getApi().getPort());

        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request
                    .headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers",
                        "Authorization");
            }

            String accessControlRequestMethod = request
                    .headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods",
                        "HEAD, GET, OPTIONS, POST");
            }

            return "OK";
        });

        before((request, response) -> {
            log.debug("Received request on route: {}\nHeaders:\n{}\nBody:\n{}",
                    request.requestMethod() + " " + request.uri(),
                    request.headers().stream().map(h -> "   " + h + ": " + request.headers(h))
                            .collect(Collectors.joining("\n")),
                    request.body());
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Headers", "*");
            if (!validateRequest(request)) {
                halt(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
            }
            response.type("application/json");
        });
    }


    /**
     * Validates if a request has the right header.
     *
     * @param request request to check
     * @return true if the header is correct.
     */
    private boolean validateRequest(Request request) {
        if (request.requestMethod().equals("OPTIONS")) {
            log.debug("Allowed access for request");
            return true;
        }
        String authorization = request.headers("Authorization");
        if (authorization == null || !authorization.equals(ShepardBot.getConfig().getApi().getAuthorization())) {
            log.debug("Denied access for request");
            return false;
        }
        log.debug("Allowed access for request");

        return true;
    }
}
