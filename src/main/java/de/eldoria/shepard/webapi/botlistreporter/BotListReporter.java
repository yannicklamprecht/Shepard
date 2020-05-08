package de.eldoria.shepard.webapi.botlistreporter;

import com.google.gson.Gson;
import de.eldoria.shepard.C;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import de.eldoria.shepard.webapi.apiobjects.botlists.requests.BotsOnDiscordxyzRequest;
import de.eldoria.shepard.webapi.apiobjects.botlists.requests.DiscordBotlistComRequests;
import de.eldoria.shepard.webapi.apiobjects.botlists.requests.DiscordBotsggRequest;
import de.eldoria.shepard.webapi.apiobjects.botlists.votes.VoteWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.discordbots.api.client.DiscordBotListAPI;

import javax.sql.DataSource;
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
public final class BotListReporter implements Runnable, ReqInit, ReqShardManager, ReqConfig, ReqDataSource {
    private final List<Consumer<VoteWrapper>> eventHandlers = new ArrayList<>();
    private DiscordBotListAPI api;
    private ShardManager shardManager;
    private Config config;
    private DataSource source;

    /**
     * Create a new botlist reporter.
     */
    public BotListReporter() {
    }

    @Override
    public void run() {
        refreshInformation();
    }

    /**
     * Refresh the server count.
     */
    private void refreshInformation() {
        int guildCount = shardManager.getGuilds().size();
        long userCount = shardManager.getUserCache().size();

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
                config.getBotlist().getToken().getBotsOnDiscordxyz(),
                new BotsOnDiscordxyzRequest(guildCount),
                200);
    }


    private void sendDiscordBotlistCom(int guildCount, long userCount) {
        queryBotlistApi(
                "discordbotlist.com",
                "https://discordbotlist.com/api/bots/512413049894731780/stats",
                "Bot " + config.getBotlist().getToken().getDiscordBotListCom(),
                new DiscordBotlistComRequests(0, guildCount, userCount, 0),
                204);
    }

    private void sendDiscordBotsgg(int guildCount) {
        queryBotlistApi(
                "discord.bots.gg",
                "https://discord.bots.gg/api/v1/bots/512413049894731780/stats",
                config.getBotlist().getToken().getDiscordBotsgg(),
                new DiscordBotsggRequest(guildCount, 1, 0),
                200);
    }

    private void sendTopgg(int guildCount) {
        log.debug("Sending Server stats to top.gg");
        api.setStats(guildCount).toCompletableFuture()
                .thenAccept(aVoid -> log.debug("Stats to top.gg send!"))
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
    public void addEventHandler(Consumer<VoteWrapper> eventHandler) {
        eventHandlers.add(eventHandler);
    }

    /**
     * redirect the vote information ti listener event handler.
     *
     * @param vote vote information
     */
    public void handleVote(VoteWrapper vote) {
        eventHandlers.forEach(eventHandler -> eventHandler.accept(vote));
    }

    @Override
    public void addConfig(Config config) {

        this.config = config;
    }

    @Override
    public void init() {
        if (config.getGeneralSettings().isBeta()) return;
        api = new DiscordBotListAPI.Builder()
                .token(config.getBotlist().getToken().getTopgg())
                .botId(shardManager.getShardById(0).getSelfUser().getId())
                .build();
        addEventHandler(new VoteHandler(shardManager, source));
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this, 120, 3600, TimeUnit.SECONDS);
    }

    @Override
    public void addShardManager(ShardManager shardManager) {

        this.shardManager = shardManager;
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }
}
