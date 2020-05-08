package de.eldoria.shepard.commandmodules.greeting.routines;

import de.eldoria.shepard.commandmodules.greeting.data.InviteData;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class RefreshInvites implements Runnable {
    private final ShardManager shardManager;
    private final InviteData inviteData;

    /**
     * Creates a new RefreshInvite object.
     *
     * @param shardManager jda instance
     * @param source       data source for information retrieval
     */
    public RefreshInvites(ShardManager shardManager, DataSource source) {
        this.shardManager = shardManager;
        inviteData = new InviteData(source);
    }

    @Override
    public void run() {
        if (shardManager == null) return;
        if (shardManager.getGuilds().isEmpty()) return;
        Iterator<Guild> iterator = shardManager.getGuilds().iterator();
        // as queue(...) runs asynchronously, we need an atomic counter
        AtomicInteger counter = new AtomicInteger();
        int guildCount = shardManager.getGuilds().size();
        while (iterator.hasNext()) {
            Guild guild = iterator.next();
            if (!Objects.requireNonNull(guild.getMember(shardManager.getShardById(0).getSelfUser()))
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
