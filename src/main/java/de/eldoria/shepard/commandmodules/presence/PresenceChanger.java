package de.eldoria.shepard.commandmodules.presence;

import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqJDA;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PresenceChanger implements Runnable, ReqJDA, ReqConfig, ReqInit {
    private boolean customStatus;
    private ScheduledExecutorService executor;
    private List<Presence> presence;
    private JDA jda;
    private Config config;

    /**
     * Create a new presence changer object.
     */
    public PresenceChanger() {
    }

    private void startScheduler() {
        if (executor.isShutdown() || executor.isTerminated()) {
            executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(this, 0, 5, TimeUnit.MINUTES);
            customStatus = false;
        }
    }

    @Override
    public void run() {
        Presence presence = this.presence.get(Math.round((float) Math.random() * this.presence.size() - 1));
        switch (presence.state) {
            case PLAYING:
                jda.getPresence().setActivity(Activity.playing(presence.message));
                break;
            case LISTENING:
                jda.getPresence().setActivity(Activity.listening(presence.message));
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
        jda.getPresence().setActivity(Activity.playing(message));
        clearScheduler();
    }

    /**
     * Set the status to listening.
     *
     * @param message listening message
     */
    public void setListening(String message) {
        jda.getPresence().setActivity(Activity.listening(message));
        clearScheduler();
    }

    /**
     * Set the status to streaming.
     *
     * @param message streaming message
     * @param url     twitch url
     */
    public void setStreaming(String message, String url) {
        jda.getPresence().setActivity(Activity.streaming(message, url));
        clearScheduler();
    }

    /**
     * Clear the custom status and use default presence.
     */
    public void clearPresence() {
        jda.getPresence().setActivity(null);
        startScheduler();
    }

    private void clearScheduler() {
        if (!customStatus) {
            executor.shutdown();
            System.out.println("Tasks canceled");
            customStatus = true;
        }
    }

    @Override
    public void addConfig(Config config) {
        this.config = config;
    }

    @Override
    public void addJDA(JDA jda) {
        this.jda = jda;
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

        executor = Executors.newSingleThreadScheduledExecutor();
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
