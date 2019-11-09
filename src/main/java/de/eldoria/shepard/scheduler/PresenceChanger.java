package de.eldoria.shepard.scheduler;

import de.eldoria.shepard.ShepardBot;
import net.dv8tion.jda.api.entities.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PresenceChanger implements Runnable {
    private static PresenceChanger instance;
    private boolean customStatus;
    private ScheduledExecutorService executor;
    private List<Presence> presence;

    /**
     * Initializes the presence changer.
     */
    public static void initialize() {
        if (instance != null) return;

        instance = new PresenceChanger();

        instance.presence = new ArrayList<>();
        for (String message : ShepardBot.getConfig().getPresence().getPlaying()) {
            instance.presence.add(new Presence(PresenceState.PLAYING, message));
        }
        for (String message : ShepardBot.getConfig().getPresence().getListening()) {
            instance.presence.add(new Presence(PresenceState.LISTENING, message));
        }

        instance.executor = Executors.newSingleThreadScheduledExecutor();
        instance.executor.scheduleAtFixedRate(instance, 0, 1, TimeUnit.MINUTES);
    }

    private void startScheduler() {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(instance, 0, 1, TimeUnit.MINUTES);
        customStatus = false;
    }

    @Override
    public void run() {
        Presence presence = this.presence.get(Math.round((float) Math.random() * this.presence.size() - 1));
        switch (presence.state) {
            case PLAYING:
                ShepardBot.getJDA().getPresence().setActivity(Activity.playing(presence.message));
                break;
            case LISTENING:
                ShepardBot.getJDA().getPresence().setActivity(Activity.listening(presence.message));
                break;
        }
    }

    public void setPlaying(String message) {
        ShepardBot.getJDA().getPresence().setActivity(Activity.playing(message));
        clearScheduler();
    }

    public void setListening(String message) {
        ShepardBot.getJDA().getPresence().setActivity(Activity.listening(message));
        clearScheduler();
    }

    public void setStreaming(String message, String url) {
        ShepardBot.getJDA().getPresence().setActivity(Activity.streaming(message, url));
        clearScheduler();
    }

    public void clearPresence() {
        ShepardBot.getJDA().getPresence().setActivity(null);
        startScheduler();
    }

    private void clearScheduler() {
        if (!customStatus) {
            executor.shutdown();
            System.out.println("Tasks canceled");
            customStatus = true;
        }
    }

    public static PresenceChanger getInstance() {
        initialize();
        return instance;
    }

    private static class Presence {
        private PresenceState state;
        private String message;

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

    private enum PresenceState {
        PLAYING, LISTENING
    }
}
