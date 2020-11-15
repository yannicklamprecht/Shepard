package de.eldoria.shepard.commandmodules.greeting.routines;

import de.eldoria.shepard.commandmodules.greeting.data.GreetingData;
import de.eldoria.shepard.commandmodules.greeting.data.InviteData;
import de.eldoria.shepard.commandmodules.greeting.types.GreetingSettings;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.sql.DataSource;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
class RegisterInvites implements Runnable {
    //private final Map<Long, Set<String>> invites = new HashMap<>();
    private final ShardManager shardManager;
    private final InviteData inviteData;
    private final GreetingData greetingData;
    private boolean checkActive;

    /**
     * Creates a new register invite runnable.
     *
     * @param shardManager shardManager instance for invite retrieval
     * @param source       data source for connection retrieval
     */
    public RegisterInvites(ShardManager shardManager, DataSource source) {
        this.shardManager = shardManager;
        inviteData = new InviteData(source);
        greetingData = new GreetingData(shardManager, source);
    }

    @Override
    public void run() {
        if (checkActive) return;
        checkActive = true;
        List<Guild> guilds;

        guilds = shardManager.getGuildCache().asList();

        for (Guild guild : guilds) {
            GreetingSettings greeting = greetingData.getGreeting(guild);

            if (!guild.getSelfMember().hasPermission(Permission.MANAGE_SERVER)) {
                continue;
            }
            evaluateInvites(guild);
        }
        checkActive = false;
    }

    private void evaluateInvites(Guild guild) {
        List<Invite> guildInvites;
        try {
            guildInvites = guild.retrieveInvites().complete();
        } catch (InsufficientPermissionException ignored) {
            // we prefer to silently fail on missing permissions since invites are not critical
            return;
        }
        guildInvites.forEach(createInviteConsumer(guild));
    }

    private Consumer<Invite> createInviteConsumer(Guild guild) {
        return i -> {
            if (!inviteData.inviteRegistered(guild, i.getCode(), null))
                if (inviteData.addInvite(guild, i.getInviter(), i.getCode(), null, i.getUses(), null)) {
                    log.debug("Auto registered invite {} on guild {}({})", i.getCode(), guild.getName(), guild.getId());
                }
        };
    }
}
