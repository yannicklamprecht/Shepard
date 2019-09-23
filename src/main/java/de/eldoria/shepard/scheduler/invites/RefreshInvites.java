package de.eldoria.shepard.scheduler.invites;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.queries.InviteData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

class RefreshInvites implements Runnable {
    RefreshInvites() {
    }


    @Override
    public void run() {
        JDA jda = ShepardBot.getJDA();

        if (jda == null) return;
        if (jda.getGuilds().isEmpty()) return;
        Iterator<Guild> iterator = jda.getGuilds().iterator();
        // as queue(...) runs asynchronously, we need an atomic counter
        AtomicInteger counter = new AtomicInteger();
        int guildCount = jda.getGuilds().size();
        while (iterator.hasNext()) {
            Guild guild = iterator.next();
            if (!guild.getMember(ShepardBot.getJDA().getSelfUser()).hasPermission(Permission.MANAGE_SERVER)) {
                continue;
            }
            guild.retrieveInvites().queue(invites -> {
                if (InviteData.updateInvite(guild, invites, null)) {
                    ShepardBot.getLogger().info("Update Invites for guild " + guild.getName()
                            + "(" + guild.getId() + ")");
                }
                // will run when the last guild was updated successfully
                if (counter.incrementAndGet() == guildCount) {
                    ShepardBot.getLogger().info("Cleaned up Invites");
                }
            });
        }
    }
}
