package de.eldoria.shepard.webapi.endpoints;

import com.google.api.client.http.HttpStatusCodes;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqJDA;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.webapi.apiobjects.ApiRank;
import de.eldoria.shepard.webapi.apiobjects.RankingResponse;
import de.eldoria.shepard.webapi.data.KudoData;
import de.eldoria.shepard.webapi.util.RankingKey;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import javax.sql.DataSource;
import java.util.List;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.path;

@Slf4j
public class KudosEndpoint implements ReqParser, ReqJDA, ReqDataSource, ReqInit {

    private ArgumentParser parser;
    private JDA jda;
    private KudoData kudoData;
    private DataSource source;
    private Cache<RankingKey, String> cache
            = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(250).build();
    private Gson gson = new GsonBuilder().create();

    /**
     * Create a new Kudos endpoint.
     */
    public KudosEndpoint() {

    }

    private void defineRoutes() {
        path("/v1", () -> path("/kudos", () -> path("/ranking", () -> {
            // Get the global ranking
            get("/global/:page/:pagesize", ((request, response) -> {
                OptionalInt page = ArgumentParser.parseInt(request.params(":page"));
                OptionalInt pagesize = ArgumentParser.parseInt(request.params(":pagesize"));

                if (page.isEmpty() || pagesize.isEmpty()) {
                    halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "Wrong type for page or pagesite.");
                }

                return cache.get(new RankingKey(page.getAsInt(), pagesize.getAsInt()),
                        () -> {
                            List<ApiRank> ranking = kudoData.getGlobalRanking(page.getAsInt(), pagesize.getAsInt());
                            int pageCount = kudoData.getGlobalRankingPagecount(pagesize.getAsInt());
                            return gson.toJson(new RankingResponse(ranking, page.getAsInt(), pageCount));
                        });

            }));

            // Get the global ranking with player match
            get("/globalfilter/:user/:page/:pagesize", ((request, response) -> {
                OptionalInt page = ArgumentParser.parseInt(request.params(":page"));
                OptionalInt pagesize = ArgumentParser.parseInt(request.params(":pagesize"));

                if (page.isEmpty() || pagesize.isEmpty()) {
                    halt(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "Wrong type for page or pagesite.");
                }
                List<User> users = parser.fuzzyGlobalUserSearch(request.params(":user"));

                return cache.get(new RankingKey(page.getAsInt(), pagesize.getAsInt(), users),
                        () -> {
                            List<ApiRank> ranking = kudoData.getGlobalRankingFilter(users, page.getAsInt(), pagesize.getAsInt());
                            int pageCount = kudoData.getGlobalRankingFilterPagecount(users, pagesize.getAsInt());
                            return gson.toJson(new RankingResponse(ranking, page.getAsInt(), pageCount));
                        });
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

                List<User> users = parser.fuzzyGuildUserSearch(guildId.getAsLong(), request.params(":user"));

                return cache.get(new RankingKey(page.getAsInt(), pagesize.getAsInt(), guildId.getAsLong(), users),
                        () -> {
                            List<ApiRank> ranking = kudoData.getGuildRankingFilter(users, guildId.getAsLong(),
                                    page.getAsInt(), pagesize.getAsInt());
                            int pageCount = kudoData.getGuildRankingFilterPagecount(users, guildId.getAsLong(),
                                    pagesize.getAsInt());
                            return gson.toJson(new RankingResponse(ranking, page.getAsInt(), pageCount));
                        });
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

                return cache.get(new RankingKey(page.getAsInt(), pagesize.getAsInt(), guildId.getAsLong()),
                        () -> {
                            List<ApiRank> ranking = kudoData.getGuildRanking(guildId.getAsLong(), page.getAsInt(),
                                    pagesize.getAsInt());
                            int pageCount = kudoData.getGuildRankingPagecount(guildId.getAsLong(), pagesize.getAsInt());
                            return gson.toJson(new RankingResponse(ranking, page.getAsInt(), pageCount));
                        });
            }));
        })));
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }

    @Override
    public void addJDA(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void init() {
        kudoData = new KudoData(jda, source);
        defineRoutes();
    }

    @Override
    public void addDataSource(DataSource source) {

        this.source = source;
    }
}
