package de.eldoria.shepard.register;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.listener.CommandListener;
import de.eldoria.shepard.listener.GroupListener;
import de.eldoria.shepard.listener.JoinListener;
import de.eldoria.shepard.listener.KeywordListener;
import de.eldoria.shepard.listener.LogListener;
import de.eldoria.shepard.listener.MessageSniffer;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListenerRegister {
    private static ListenerRegister instance;
    private static boolean registered;
    private List<ListenerAdapter> listener = new ArrayList<>();

    private ListenerRegister() {
        listener.add(new CommandListener());
        listener.add(new GroupListener());
        listener.add(new JoinListener());
        listener.add(new KeywordListener());
        listener.add(new LogListener());
        listener.add(new MessageSniffer());
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


    private void register() {
    }

}
