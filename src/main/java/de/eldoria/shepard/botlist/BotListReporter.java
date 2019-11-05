package de.eldoria.shepard.botlist;

import com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import de.eldoria.shepard.ShepardBot;
import net.dv8tion.jda.api.entities.User;
import org.discordbots.api.client.DiscordBotListAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static spark.Spark.port;
import static spark.Spark.post;

public final class BotListReporter {
    private static BotListReporter instance;
    private final DiscordBotListAPI api;
    private final List<Consumer<BotListResponse>> eventHandlers;

    private BotListReporter() {
        api = new DiscordBotListAPI.Builder()
                .token(ShepardBot.getConfig().getBotlist().getToken())
                .botId(ShepardBot.getJDA().getSelfUser().getId())
                .build();
        eventHandlers = new ArrayList<>();

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

    public void addEventHandler(Consumer<BotListResponse> eventHandler) {
        eventHandlers.add(eventHandler);
    }

    private void defineRoutes() {
        port(34555);

        post("/votes/", (request, response) -> {
            String authorization = request.headers("Authorization");
            if (!authorization.equals(ShepardBot.getConfig().getBotlist().getAuthorization())) {
                return HttpStatusCodes.STATUS_CODE_UNAUTHORIZED;
            }

            response.type("application/json");
            BotListResponse botListResponse = new Gson().fromJson(request.body(), BotListResponse.class);
            handleVote(botListResponse);

            return HttpStatusCodes.STATUS_CODE_OK;
        });
    }

    private void handleVote(BotListResponse botListResponse) {
        eventHandlers.forEach(eventHandler -> eventHandler.accept(botListResponse));
    }
}
