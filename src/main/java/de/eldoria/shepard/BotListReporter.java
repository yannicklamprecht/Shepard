package de.eldoria.shepard;

import org.discordbots.api.client.DiscordBotListAPI;
import net.dv8tion.jda.api.entities.User;
import spark.Request;
import spark.Response;

import java.util.concurrent.atomic.AtomicBoolean;

import static spark.Spark.post;
import static spark.route.HttpMethod.get;
import static spark.route.HttpMethod.post;

public class BotListReporter {
    private DiscordBotListAPI api;
    private static BotListReporter instance;

    private BotListReporter() {
        api = new DiscordBotListAPI.Builder()
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjUxMjQxMzA0OTg5NDczMTc4MCIsImJvdCI6dHJ1ZSwiaWF0IjoxNTcxNjk2NDYwfQ.ERtbOsNSZfmytzNNKzHo7y79eGC8DTYMjzN00QTUFN8")
                .botId("512413049894731780")
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
        post("/votes/", (Request request, Response response) -> {
            response.type("application/json");
            return request.body();
        });
    }
}
