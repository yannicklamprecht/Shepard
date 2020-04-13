package de.eldoria.shepard.webapi;

import com.google.gson.Gson;
import de.eldoria.shepard.C;
import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.webapi.apiobjects.botlists.BotsOnDiscordxyzRequest;
import de.eldoria.shepard.webapi.apiobjects.botlists.DiscordBotlistComRequests;
import de.eldoria.shepard.webapi.apiobjects.botlists.DiscordBotsggRequest;
import de.eldoria.shepard.webapi.apiobjects.botlists.VoteInformation;
import lombok.extern.slf4j.Slf4j;
import org.discordbots.api.client.DiscordBotListAPI;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public final class BotListReporter implements Runnable {
    private static BotListReporter instance;
    private final DiscordBotListAPI api;
    private final List<Consumer<VoteInformation>> eventHandlers;

    private BotListReporter() {
        api = new DiscordBotListAPI.Builder()
                .token(ShepardBot.getConfig().getBotlist().getToken().getTopgg())
                .botId(ShepardBot.getJDA().getSelfUser().getId())
                .build();
        eventHandlers = new ArrayList<>();
        addEventHandler(new VoteHandler());
        if (!ShepardBot.getConfig().isBeta()) {
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(this, 120, 3600, TimeUnit.SECONDS);
        }
    }

    /**
     * Initializes the bot list reporter if not active.
     *
     * @return self instance
     */
    public static BotListReporter initialize() {
        if (instance == null) {
            instance = new BotListReporter();
        }
        return instance;
    }

    @Override
    public void run() {
        refreshInformation();
    }

    /**
     * Refresh the server count.
     */
    private void refreshInformation() {
        int guildCount = ShepardBot.getJDA().getGuilds().size();
        long userCount = ShepardBot.getJDA().getUserCache().size();

        log.debug("Current Server count is: " + guildCount);
        sendTopgg(guildCount);
        sendDiscordBotlistCom(guildCount, userCount);
        sendDiscordBotsgg(guildCount);
        // TODO: uncomment when bot is approved.
        //sendBotsOnDiscordxyz(guildCount);
    }

    private void sendBotsOnDiscordxyz(int guildCount) {
        queryBotlistApi(
                "bots.ondiscord.xyz",
                "https://bots.ondiscord.xyz/bot-api/bots/512413049894731780/guilds ",
                ShepardBot.getConfig().getBotlist().getToken().getBotsOnDiscordxyz(),
                new BotsOnDiscordxyzRequest(guildCount),
                200);
    }


    private void sendDiscordBotlistCom(int guildCount, long userCount) {
        queryBotlistApi(
                "discordbotlist.com",
                "https://discordbotlist.com/api/bots/512413049894731780/stats",
                "Bot " + ShepardBot.getConfig().getBotlist().getToken().getDiscordBotListCom(),
                new DiscordBotlistComRequests(0, guildCount, userCount, 0),
                204);
    }

    private void sendDiscordBotsgg(int guildCount) {
        queryBotlistApi(
                "discord.bots.gg",
                "https://discord.bots.gg/api/v1/bots/512413049894731780/stats",
                ShepardBot.getConfig().getBotlist().getToken().getDiscordBotsgg(),
                new DiscordBotsggRequest(guildCount, 1, 0),
                200);
    }

    private void sendTopgg(int guildCount) {
        log.debug("Sending Server stats to top.gg");
        api.setStats(guildCount).toCompletableFuture()
                .thenAccept(aVoid -> {
                    log.debug("Stats to top.gg send!");
                })
                .exceptionally(e -> {
                    log.warn(C.NOTIFY_ADMIN, "failed to send stats to top.gg", e);
                    return null;
                });
    }

    private void queryBotlistApi(String serviceName, String url, String authorization, Object requestPayload,
                                 int successCode) {
        log.debug("Sending Server stats to {}.", serviceName);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(requestPayload)))
                .uri(URI.create(url))
                .setHeader("Authorization", authorization)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.warn(C.NOTIFY_ADMIN, "Failed to send stats to {}!", serviceName, e);
            return;
        }
        if (response.statusCode() != successCode) {
            log.warn(C.NOTIFY_ADMIN, "Failed to send stats to {}\nStatus code: {}\n Body:\n{}",
                    serviceName, response.statusCode(), response.body());
        } else {
            log.debug("Stats to {} send!", serviceName);
        }
    }

    /**
     * Adds a event handler.
     *
     * @param eventHandler eventhandler to add
     */
    public void addEventHandler(Consumer<VoteInformation> eventHandler) {
        eventHandlers.add(eventHandler);
    }

    /**
     * redirect the vote information ti listener event handler.
     *
     * @param voteInformation vote information
     */
    public void handleVote(VoteInformation voteInformation) {
        eventHandlers.forEach(eventHandler -> eventHandler.accept(voteInformation));
    }
}
