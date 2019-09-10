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

public final class RegisterInvites implements Runnable {
    private static Thread thread;

    private static RegisterInvites instance;

    private HashMap<Long, Set<String>> invites = new HashMap<>();

    private RegisterInvites() {
        start();
    }

    /**
     * Initializes the listener.
     */
    public static void initialize() {
        if (instance == null) {
            instance = new RegisterInvites();
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
        List<Guild> guilds;
        try{
        guilds = ShepardBot.getJDA().getGuilds();
        }catch (IllegalArgumentException e){
            return;
        }

        ShepardBot.getLogger().info("Looking for unregistered invites.");

        for (Guild guild : guilds) {
            if (!guild.getMember(ShepardBot.getJDA().getSelfUser()).hasPermission(Permission.MANAGE_SERVER)) {
                continue;
            }
            if (invites.containsKey(guild.getIdLong())) {
                List<Invite> guildInvites = guild.retrieveInvites().complete();
                try {
                    guildInvites.stream()
                            .filter(i -> !invites.get(guild.getIdLong()).contains(i.getCode()))
                            .forEach(i -> {
                                InviteData.addInvite(guild,
                                        i.getCode(),
                                        i.getInviter() != null ? i.getInviter().getAsTag() : "unknown user",
                                        i.getUses(), null);
                                invites.get(guild.getIdLong()).add(i.getCode());
                                ShepardBot.getLogger().info("Auto registered invite " + i.getCode()
                                        + " on guild " + guild.getName() + "(" + guild.getId() + ")");
                            });
                } catch (InsufficientPermissionException e) {
                    ShepardBot.getLogger().error("Error occurred on guild " + guild.getName()
                            + "(" + guild.getId() + ")", e);
                }
            } else {
                invites.put(guild.getIdLong(),
                        InviteData.getInvites(guild, null)
                                .stream().map(DatabaseInvite::getCode)
                                .collect(Collectors.toSet()));
            }
        }
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            return;
        }
        checkInvites();
    }
}
