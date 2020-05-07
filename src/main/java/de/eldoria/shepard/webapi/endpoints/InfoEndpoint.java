package de.eldoria.shepard.webapi.endpoints;

import com.google.api.client.http.HttpStatusCodes;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqJDA;
import de.eldoria.shepard.webapi.apiobjects.GuildInfo;
import de.eldoria.shepard.webapi.apiobjects.UserInfo;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.path;

public class InfoEndpoint implements ReqJDA, ReqInit {
    private final Gson gson = new GsonBuilder().create();
    private final Cache<Long, String> userCache = CacheBuilder.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    private final Cache<Long, String> guildCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    private JDA jda;

    /**
     * Create a new info endpoint.
     */
    public InfoEndpoint() {
    }

    @Override
    public void addJDA(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void init() {
        defineRoutes();
    }

    private void defineRoutes() {
        path("/v1", () -> path("/info", () -> {
            get("/user/:id", (request, response) -> {
                OptionalLong optionalId = ArgumentParser.parseLong(request.params(":id"));
                if (optionalId.isEmpty()) {
                    halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST);
                }
                long id = optionalId.getAsLong();

                String result = userCache.get(id, () -> {
                    User user = jda.getUserById(id);
                    if (user == null) {
                        return null;
                    }
                    return gson.toJson(new UserInfo(user));
                });

                if (result == null) {
                    halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST);
                }
                return result;
            });

            get("/guild/:id", (request, response) -> {
                OptionalLong optionalId = ArgumentParser.parseLong(request.params(":id"));
                if (optionalId.isEmpty()) {
                    halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST);
                }
                long id = optionalId.getAsLong();

                String result = guildCache.get(id, () -> {
                    Guild guild = jda.getGuildById(id);
                    if (guild == null) {
                        return null;
                    }
                    return gson.toJson(new GuildInfo(guild));
                });

                if (result == null) {
                    halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST);
                }
                return result;
            });
        }));
    }
}
