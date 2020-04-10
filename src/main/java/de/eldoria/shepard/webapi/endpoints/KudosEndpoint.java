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
                        Integer page = ArgumentParser.parseInt(request.params(":page"));
                        Integer pagesize = ArgumentParser.parseInt(request.params(":pagesize"));

                        if (page == null || pagesize == null) {
                            halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "Wrong type for page or pagesite.");
                        }

                        List<ApiRank> ranking = KudosData.getGlobalRanking(page, pagesize);
                        int pageCount = KudosData.getGlobalRankingPagecount(pagesize);
                        return new Gson().toJson(new GlobalRankingResponse(ranking, page, pageCount));
                    }));

                    // Get the global ranking with player match
                    get("/globalfilter/:user/:page/:pagesize", ((request, response) -> {
                        Integer page = ArgumentParser.parseInt(request.params(":page"));
                        Integer pagesize = ArgumentParser.parseInt(request.params(":pagesize"));

                        if (page == null || pagesize == null) {
                            halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "Wrong type for page or pagesite.");
                        }
                        List<User> users = ArgumentParser.fuzzyGlobalUserSearch(request.params(":user"));

                        List<ApiRank> ranking = KudosData.getGlobalRankingFilter(users, page, pagesize);

                        int pageCount = KudosData.getGlobalRankingFilterPagecount(users, pagesize);

                        return new Gson().toJson(new GlobalRankingResponse(ranking, page, pageCount));
                    }));

                    // Get the guild ranking with player match
                    get("/guildfilter/:guildid/:user/:page/:pagesize", ((request, response) -> {
                        Integer page = ArgumentParser.parseInt(request.params(":page"));
                        Integer pagesize = ArgumentParser.parseInt(request.params(":pagesize"));

                        if (page == null || pagesize == null) {
                            halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "Wrong type for page or pagesite.");
                        }

                        Long guildId = ArgumentParser.parseLong(request.params(":guildid"));

                        if (guildId == null) {
                            halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "Invalid Guild Id");
                            return HttpStatusCodes.STATUS_CODE_BAD_REQUEST;
                        }

                        List<User> users = ArgumentParser.fuzzyGuildUserSearch(guildId, request.params(":user"));

                        List<ApiRank> ranking = KudosData.getGuildRankingFilter(users, guildId, page, pagesize);

                        int pageCount = KudosData.getGuildRankingFilterPagecount(users, guildId, pagesize);

                        return new Gson().toJson(new GlobalRankingResponse(ranking, page, pageCount));
                    }));

                    // Get the guild ranking
                    get("/guild/:guildid/:page/:pagesize", ((request, response) -> {
                        Integer page = ArgumentParser.parseInt(request.params(":page"));
                        Integer pagesize = ArgumentParser.parseInt(request.params(":pagesize"));

                        if (page == null || pagesize == null) {
                            halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "Wrong type for page or pagesite.");
                        }

                        Long guildId = ArgumentParser.parseLong(request.params(":guildid"));

                        if (guildId == null) {
                            halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "Invalid Guild Id");
                            return HttpStatusCodes.STATUS_CODE_BAD_REQUEST;
                        }

                        List<ApiRank> ranking = KudosData.getGuildRanking(guildId, page, pagesize);

                        int pageCount = KudosData.getGuildRankingPagecount(guildId, pagesize);

                        return new Gson().toJson(new GlobalRankingResponse(ranking, page, pageCount));
                    }));
                });
            });
        });
    }
}
