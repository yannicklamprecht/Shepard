package de.eldoria.shepard.botlist;

import com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.queries.KudoData;
import org.discordbots.api.client.DiscordBotListAPI;
import net.dv8tion.jda.api.entities.User;
import spark.Request;
import spark.Response;

import java.util.concurrent.atomic.AtomicBoolean;

import static spark.Spark.post;
import static spark.route.HttpMethod.get;

public class BotListReporter {
    private DiscordBotListAPI api;
    private static BotListReporter instance;

    private BotListReporter() {
        api = new DiscordBotListAPI.Builder()
                .token(ShepardBot.getConfig().getBotList().getToken())
                .botId(ShepardBot.getJDA().getSelfUser().getId())
                .build();

        defineRoutes();
    }

    public static void initialize() {
        if (instance == null) {
            instance = new BotListReporter();
        }
    }

    public static BotListReporter getInstance() {
        initialize();
        return instance;
    }

    public void refreshInformation() {
        api.setStats(ShepardBot.getJDA().getGuilds().size());
    }

    public boolean hasVoted(User user) {
        AtomicBoolean voted = new AtomicBoolean(false);

        api.hasVoted(user.getId()).whenComplete((bool, e) -> {
            if (e != null) {
                voted.set(false);
                ShepardBot.getLogger().error(e);
                return;
            }
            voted.set(bool);
        });

        return voted.get();
    }

    public void defineRoutes() {
        post("/votes/", (request, response) -> {
            String authorization = request.headers("Authorization");
            if(!authorization.equals(ShepardBot.getConfig().getBotList().getAuthorization())){
                return HttpStatusCodes.STATUS_CODE_UNAUTHORIZED;
            }

            response.type("application/json");
            BotListResponse botListResponse = new Gson().fromJson(request.body(), BotListResponse.class);
            handleVote(botListResponse);

            return HttpStatusCodes.STATUS_CODE_OK;
        });
    }

    public void handleVote(BotListResponse botListResponse) {

    }
}
