package de.eldoria.shepard.Scheduler;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.queries.InviteData;
import de.eldoria.shepard.database.types.DatabaseInvite;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class RefreshInvites implements Runnable {
    private static Thread thread;

    private static RefreshInvites instance;

    private HashMap<Long, Set<String>> invites = new HashMap<>();

    private RefreshInvites() {
        start();
    }

    /**
     * Initializes the listener.
     */
    public static void initialize() {
        if (instance == null) {
            instance = new RefreshInvites();
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
        refreshInvites();
    }

    private void refreshInvites() {
        List<Guild> guilds = ShepardBot.getJDA().getGuilds();

        for (Guild guild : guilds) {
            if (!guild.getMember(ShepardBot.getJDA().getSelfUser()).hasPermission(Permission.MANAGE_SERVER)) {
                continue;
            }
            InviteData.updateInvite(guild, guild.retrieveInvites().complete(), null);
        }
        try {
            Thread.sleep(600000);
        } catch (InterruptedException e) {
            return;
        }
        refreshInvites();
    }
}
