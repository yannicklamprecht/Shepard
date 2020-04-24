package de.eldoria.shepard.webapi.endpoints;

import com.google.api.client.http.HttpStatusCodes;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.eldoria.shepard.basemodules.commanddispatching.CommandHub;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.modulebuilder.requirements.ReqCommands;
import de.eldoria.shepard.webapi.apiobjects.CommandSearchResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.path;

public class CommandEndpoint implements ReqCommands {

    private final Cache<String, String> commandCache = CacheBuilder.newBuilder().maximumSize(50).build();
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private CommandHub commandHub;

    /**
     * Create a new command endpoint.
     */
    public CommandEndpoint() {
        defineRoutes();
    }

    private void defineRoutes() {
        path("/v1", () -> path("/command", () -> {
            get("/list", (request, response) -> gson.toJson(
                    commandHub.getSimpleCommandInfos(CommandCategory.BOT_CONFIG, CommandCategory.EXCLUSIVE)));

            get("/search/:text", (request, response) -> {
                Optional<Command> commandOptional = commandHub.getCommand(request.params(":text"));
                Command command = null;
                if (commandOptional.isPresent()
                        && !(commandOptional.get().getCategory() == CommandCategory.EXCLUSIVE
                        || commandOptional.get().getCategory() == CommandCategory.BOT_CONFIG)) {
                    command = commandOptional.get();
                }
                List<Command> similarCommands = commandHub.getSimilarCommands(request.params(":text"))
                        .stream().filter(c -> !(c.getCategory() == CommandCategory.EXCLUSIVE
                                || c.getCategory() == CommandCategory.BOT_CONFIG)).collect(Collectors.toList());
                return gson.toJson(new CommandSearchResponse(command, similarCommands));
            });

            get("/info/:text", (request, response) -> {
                Optional<Command> command = commandHub.getCommand(request.params(":text"));
                if (command.isEmpty()) {
                    halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "Invalid command.");
                }

                return commandCache.get(command.get().getCommandIdentifier(), () ->
                        gson.toJson(command.get().getCommandInfo()));
            });
        }));
    }

    @Override
    public void addCommands(CommandHub commandHub) {
        this.commandHub = commandHub;
    }
}
