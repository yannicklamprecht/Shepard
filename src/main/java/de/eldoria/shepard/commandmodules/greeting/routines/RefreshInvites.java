package de.eldoria.shepard.commandmodules.greeting.routines;

import de.eldoria.shepard.commandmodules.greeting.data.InviteData;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class RefreshInvites implements Runnable {
    private final JDA jda;
    private final InviteData inviteData;

    /**
     * Creates a new RefreshInvite object.
     *
     * @param jda jda instance
     * @param source data source for information retrieval
     */
    public RefreshInvites(JDA jda, DataSource source) {
        this.jda = jda;
        inviteData = new InviteData(source);
    }

    @Override
    public void run() {
        if (jda == null) return;
        if (jda.getGuilds().isEmpty()) return;
        Iterator<Guild> iterator = jda.getGuilds().iterator();
        // as queue(...) runs asynchronously, we need an atomic counter
        AtomicInteger counter = new AtomicInteger();
        int guildCount = jda.getGuilds().size();
        while (iterator.hasNext()) {
            Guild guild = iterator.next();
            if (!Objects.requireNonNull(guild.getMember(jda.getSelfUser()))
                    .hasPermission(Permission.MANAGE_SERVER)) {
                continue;
            }
            guild.retrieveInvites().queue(invites -> {
                if (inviteData.updateInvite(guild, invites, null)) {
                    log.debug("Update Invites for guild {}({})", guild.getName(), guild.getId());
                }
                // will run when the last guild was updated successfully
                if (counter.incrementAndGet() == guildCount) {
                    log.debug("Cleaned up Invites");
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
