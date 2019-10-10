package de.eldoria.shepard.register;

import de.eldoria.shepard.listener.HentaiImageRegisterListener;
import de.eldoria.shepard.listener.PrivateMessageListener;
import de.eldoria.shepard.listener.ReactionListener;
import de.eldoria.shepard.scheduler.KudoCounter;
import de.eldoria.shepard.scheduler.invites.InviteScheduler;
import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.listener.CommandListener;
import de.eldoria.shepard.listener.ChangelogListener;
import de.eldoria.shepard.listener.GreetingListener;
import de.eldoria.shepard.listener.KeywordListener;
import de.eldoria.shepard.listener.LogListener;
import de.eldoria.shepard.listener.MessageSniffer;
import de.eldoria.shepard.listener.TicketCleanupListener;
import de.eldoria.shepard.scheduler.monitoring.MonitoringScheduler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public final class ListenerRegister {
    private static ListenerRegister instance;
    private static boolean registered;
    private final List<ListenerAdapter> listener = new ArrayList<>();

    private ListenerRegister() {
        listener.add(new CommandListener());
        listener.add(new ChangelogListener());
        listener.add(new GreetingListener());
        listener.add(new KeywordListener());
        listener.add(new LogListener());
        listener.add(new MessageSniffer());
        listener.add(new TicketCleanupListener());
        listener.add(new ReactionListener());
        listener.add(new HentaiImageRegisterListener());
        listener.add(new PrivateMessageListener());
        InviteScheduler.initialize();
        MonitoringScheduler.initialize();
        new KudoCounter().initialize();
    }

    private static ListenerRegister getInstance() {
        if (instance == null) {
            instance = new ListenerRegister();
        }
        return instance;
    }

    /**
     * Registers all listener add jda.
     */
    public static void registerListener() {
        if (registered) return;
        registered = true;
        ShepardBot.getInstance().registerListener(getInstance().listener);
    }
}