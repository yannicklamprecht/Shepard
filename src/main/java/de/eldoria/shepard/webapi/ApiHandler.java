package de.eldoria.shepard.webapi;

import com.google.api.client.http.HttpStatusCodes;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import lombok.extern.slf4j.Slf4j;
import spark.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static spark.Spark.before;
import static spark.Spark.halt;
import static spark.Spark.options;
import static spark.Spark.port;


@Slf4j
public final class ApiHandler implements ReqConfig, ReqInit {

    private Config config;


    /**
     * Create a new api handler.
     */
    public ApiHandler() {
    }

    private void initializeApi() {
        port(config.getApi().getPort());

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
        List<String> authorization = new ArrayList<>();
        authorization.add(request.headers("Authorization"));
        String s = request.headers("X-DBL-Signature");
        if (s != null) {
            s = s.split("\\s")[0];
        }
        authorization.add(s);
        if (!authorization.contains(config.getApi().getAuthorization())) {
            log.debug("Denied access for request");
            return false;
        }
        log.debug("Allowed access for request");
        return true;
    }

    @Override
    public void addConfig(Config config) {
        this.config = config;
    }

    @Override
    public void init() {
        log.info("Initializing api");
        initializeApi();
    }
}
