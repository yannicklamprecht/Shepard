package de.eldoria.shepard.webapi;

import de.eldoria.shepard.C;
import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.webapi.apiobjects.VoteInformation;
import lombok.extern.slf4j.Slf4j;
import org.discordbots.api.client.DiscordBotListAPI;

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
                .token(ShepardBot.getConfig().getBotlist().getToken())
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
    public void refreshInformation() {
        log.debug("Sending Server stats to top.gg. Current Server count is: " + ShepardBot.getJDA().getGuilds().size());
        api.setStats(ShepardBot.getJDA().getGuilds().size()).toCompletableFuture()
                .thenAccept(aVoid -> {
                    log.debug("Stats send!");
                })
                .exceptionally(e -> {
                    log.warn(C.NOTIFY_ADMIN, "failed to send server stats to top.gg", e);
                    return null;
                });
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
