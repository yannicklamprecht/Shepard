package de.eldoria.shepard.commandmodules.presence;

import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PresenceChanger implements Runnable, ReqShardManager, ReqConfig, ReqInit {
    private boolean customStatus;
    private List<Presence> presence;
    private ShardManager shardManager;
    private Config config;

    /**
     * Create a new presence changer object.
     */
    public PresenceChanger() {
    }

    @Override
    public void run() {
        if (customStatus) {
            log.debug("Presence is locked due to custom status.");
            return;
        }
        log.debug("Changing random presence");
        randomPresence();
    }

    private void randomPresence() {
        Presence presence = this.presence.get(Math.round((float) Math.random() * this.presence.size() - 1));
        switch (presence.state) {
            case PLAYING:
                shardManager.setActivity(Activity.playing(presence.message));
                break;
            case LISTENING:
                shardManager.setActivity(Activity.listening(presence.message));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + presence.state);
        }
    }

    /**
     * Set the status to playing.
     *
     * @param message playing message
     */
    public void setPlaying(String message) {
        shardManager.setActivity(Activity.playing(message));
        customStatus = true;
    }

    /**
     * Set the status to listening.
     *
     * @param message listening message
     */
    public void setListening(String message) {
        shardManager.setActivity(Activity.listening(message));
        customStatus = true;
    }

    /**
     * Set the status to streaming.
     *
     * @param message streaming message
     * @param url     twitch url
     */
    public void setStreaming(String message, String url) {
        shardManager.setActivity(Activity.streaming(message, url));
        customStatus = true;
    }

    /**
     * Clear the custom status and use default presence.
     */
    public void clearPresence() {
        shardManager.setActivity(null);
        customStatus = false;
        randomPresence();
    }

    @Override
    public void addConfig(Config config) {
        this.config = config;
    }

    @Override
    public void addShardManager(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    public void init() {
        presence = new ArrayList<>();
        for (String message : config.getPresence().getPlaying()) {
            presence.add(new Presence(PresenceState.PLAYING, message));
        }
        for (String message : config.getPresence().getListening()) {
            presence.add(new Presence(PresenceState.LISTENING, message));
        }

        log.debug("Presence changer initialized.");

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this, 0, 5, TimeUnit.MINUTES);
    }

    private enum PresenceState {
        PLAYING, LISTENING
    }

    private static final class Presence {
        private final PresenceState state;
        private final String message;

        private Presence(PresenceState state, String message) {
            this.state = state;
            this.message = message;
        }

        public PresenceState getState() {
            return state;
        }

        public String getMessage() {
            return message;
        }
    }
}
