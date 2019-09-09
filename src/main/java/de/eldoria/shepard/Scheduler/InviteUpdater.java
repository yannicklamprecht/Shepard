package de.eldoria.shepard.Scheduler;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.queries.InviteData;
import de.eldoria.shepard.database.types.DatabaseInvite;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class InviteUpdater implements Runnable {
    private static Thread thread;

    private static InviteUpdater instance;

    private HashMap<Long, Set<String>> invites = new HashMap<>();

    private InviteUpdater() {
        start();
    }

    /**
     * Initializes the listener.
     */
    public static void initialize() {
        if (instance == null) {
            instance = new InviteUpdater();
        }
    }

    private void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void run() {
        checkInvites();
    }

    private void checkInvites() {
        List<Guild> guilds = ShepardBot.getJDA().getGuilds();

        for (Guild guild : guilds) {
            if (invites.containsKey(guild.getIdLong())) {
                guild.retrieveInvites().complete().stream()
                        .filter(i -> !invites.get(guild.getIdLong()).contains(i.getCode()))
                        .collect(Collectors.toList())
                        .forEach(i -> {
                            InviteData.addInvite(guild, i.getCode(),
                                    i.getInviter() != null ? i.getInviter().getAsTag() : "unknown user",
                                    i.getUses(), null);
                            invites.get(guild.getIdLong()).add(i.getCode());
                        });
            } else {
                invites.put(guild.getIdLong(),
                        InviteData.getInvites(guild, null)
                                .stream().map(DatabaseInvite::getCode)
                                .collect(Collectors.toSet()));
            }
        }
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            ShepardBot.getLogger().error(e);
        }
        checkInvites();
    }
}
