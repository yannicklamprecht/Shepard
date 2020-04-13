package de.eldoria.shepard.webapi.endpoints;

import com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.collections.CommandInfos;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.webapi.apiobjects.ApiCache;
import de.eldoria.shepard.webapi.apiobjects.CommandSearchResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.path;

public class CommandEndpoint {

    private Map<String, ApiCache> cache = new HashMap<>();

    /**
     * Create a new command endpoint.
     */
    public CommandEndpoint() {
        defineRoutes();
    }

    private void defineRoutes() {
        path("/v1", () -> {
            path("/command", () -> {
                get("/list", (request, response) -> {
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

                get("/search/:text", (request, response) -> {
                    CommandCollection instance = CommandCollection.getInstance();
                    Command command = instance.getCommand(request.params(":text"));
                    if (command != null
                            && (command.getCategory() == ContextCategory.EXCLUSIVE
                            || command.getCategory() == ContextCategory.BOT_CONFIG)) {
                        command = null;
                    }
                    List<Command> similarCommands = instance.getSimilarCommands(request.params(":text"))
                            .stream().filter(c -> !(c.getCategory() == ContextCategory.EXCLUSIVE
                                    || c.getCategory() == ContextCategory.BOT_CONFIG)).collect(Collectors.toList());
                    return new GsonBuilder().serializeNulls().create()
                            .toJson(new CommandSearchResponse(command, similarCommands));
                });

                get("/info/:text", (request, response) -> {
                    CommandCollection instance = CommandCollection.getInstance();
                    Command command = instance.getCommand(request.params(":text"));
                    if (command == null) {
                        halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "Invalid command.");
                    }
                    return new GsonBuilder().serializeNulls().create().toJson(command.getCommandInfo());
                });
            });
        });
    }
}
