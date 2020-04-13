package de.eldoria.shepard.webapi.endpoints;

import com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.database.queries.api.KudosData;
import de.eldoria.shepard.database.types.ApiRank;
import de.eldoria.shepard.webapi.apiobjects.GlobalRankingResponse;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.path;

@Slf4j
public class KudosEndpoint {

    /**
     * Create a new Kudos endpoint.
     */
    public KudosEndpoint() {
        defineRoutes();
    }

    private void defineRoutes() {
        path("/v1", () -> {
            path("/kudos", () -> {
                path("/ranking", () -> {
                    // Get the global ranking
                    get("/global/:page/:pagesize", ((request, response) -> {
                        OptionalInt page = ArgumentParser.parseInt(request.params(":page"));
                        OptionalInt pagesize = ArgumentParser.parseInt(request.params(":pagesize"));

                        if (page.isEmpty() || pagesize.isEmpty()) {
                            halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "Wrong type for page or pagesite.");
                        }

                        List<ApiRank> ranking = KudosData.getGlobalRanking(page.getAsInt(), pagesize.getAsInt());
                        int pageCount = KudosData.getGlobalRankingPagecount(pagesize.getAsInt());
                        return new Gson().toJson(new GlobalRankingResponse(ranking, page.getAsInt(), pageCount));
                    }));

                    // Get the global ranking with player match
                    get("/globalfilter/:user/:page/:pagesize", ((request, response) -> {
                        OptionalInt page = ArgumentParser.parseInt(request.params(":page"));
                        OptionalInt pagesize = ArgumentParser.parseInt(request.params(":pagesize"));

                        if (page.isEmpty() || pagesize.isEmpty()) {
                            halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "Wrong type for page or pagesite.");
                        }
                        List<User> users = ArgumentParser.fuzzyGlobalUserSearch(request.params(":user"));

                        List<ApiRank> ranking = KudosData.getGlobalRankingFilter(users, page.getAsInt(), pagesize.getAsInt());

                        int pageCount = KudosData.getGlobalRankingFilterPagecount(users, pagesize.getAsInt());

                        return new Gson().toJson(new GlobalRankingResponse(ranking, page.getAsInt(), pageCount));
                    }));

                    // Get the guild ranking with player match
                    get("/guildfilter/:guildid/:user/:page/:pagesize", ((request, response) -> {
                        OptionalInt page = ArgumentParser.parseInt(request.params(":page"));
                        OptionalInt pagesize = ArgumentParser.parseInt(request.params(":pagesize"));

                        if (page.isEmpty() || pagesize.isEmpty()) {
                            halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "Wrong type for page or pagesite.");
                        }

                        OptionalLong guildId = ArgumentParser.parseLong(request.params(":guildid"));

                        if (guildId.isEmpty()) {
                            halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "Invalid Guild Id");
                            return HttpStatusCodes.STATUS_CODE_BAD_REQUEST;
                        }

                        List<User> users = ArgumentParser.fuzzyGuildUserSearch(guildId.getAsLong(), request.params(":user"));

                        List<ApiRank> ranking = KudosData.getGuildRankingFilter(users, guildId.getAsLong(), page.getAsInt(), pagesize.getAsInt());

                        int pageCount = KudosData.getGuildRankingFilterPagecount(users, guildId.getAsLong(), pagesize.getAsInt());

                        return new Gson().toJson(new GlobalRankingResponse(ranking, page.getAsInt(), pageCount));
                    }));

                    // Get the guild ranking
                    get("/guild/:guildid/:page/:pagesize", ((request, response) -> {
                        OptionalInt page = ArgumentParser.parseInt(request.params(":page"));
                        OptionalInt pagesize = ArgumentParser.parseInt(request.params(":pagesize"));

                        if (page.isEmpty() || pagesize.isEmpty()) {
                            halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "Wrong type for page or pagesite.");
                        }

                        OptionalLong guildId = ArgumentParser.parseLong(request.params(":guildid"));

                        if (guildId.isEmpty()) {
                            halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "Invalid Guild Id");
                            return HttpStatusCodes.STATUS_CODE_BAD_REQUEST;
                        }

                        List<ApiRank> ranking = KudosData.getGuildRanking(guildId.getAsLong(), page.getAsInt(), pagesize.getAsInt());

                        int pageCount = KudosData.getGuildRankingPagecount(guildId.getAsLong(), pagesize.getAsInt());

                        return new Gson().toJson(new GlobalRankingResponse(ranking, page.getAsInt(), pageCount));
                    }));
                });
            });
        });
    }
}
