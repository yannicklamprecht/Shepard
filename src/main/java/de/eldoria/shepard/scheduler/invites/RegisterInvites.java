package de.eldoria.shepard.scheduler.invites;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.queries.InviteData;
import de.eldoria.shepard.database.types.DatabaseInvite;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
class RegisterInvites implements Runnable {
    private final Map<Long, Set<String>> invites = new HashMap<>();

    @Override
    public void run() {
        while (true) {
            List<Guild> guilds;

            try {
                guilds = ShepardBot.getJDA().getGuilds();
            } catch (IllegalArgumentException e) {
                return;
            }
            int sleepDuration = Math.max(10000 / guilds.size(), 250);

            for (Guild guild : guilds) {
                if (!Objects.requireNonNull(guild.getMember(ShepardBot.getJDA()
                        .getSelfUser())).hasPermission(Permission.MANAGE_SERVER)) {
                    continue;
                }
                if (invites.containsKey(guild.getIdLong())) {
                    evaluateInvites(guild);
                } else {
                    invites.put(guild.getIdLong(), InviteData.getInvites(guild, null).stream()
                            .map(DatabaseInvite::getCode)
                            .collect(Collectors.toSet()));
                }
                try {
                    Thread.sleep(sleepDuration);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private void evaluateInvites(Guild guild) {
        List<Invite> guildInvites;
        try {
            guildInvites = guild.retrieveInvites().complete();
        } catch (InsufficientPermissionException ignored) {
            // we prefer to silently fail on missing permissions since invites are not critical
            return;
        }
        guildInvites.stream()
                .filter(i -> !invites.get(guild.getIdLong()).contains(i.getCode()))
                .forEach(createInviteConsumer(guild));
    }

    private Consumer<Invite> createInviteConsumer(Guild guild) {
        return i -> {
            String name = i.getInviter() != null ? i.getInviter().getAsTag() : "unknown user";
            if (InviteData.addInvite(guild, i.getCode(), name, i.getUses(), null)) {
                invites.get(guild.getIdLong()).add(i.getCode());
                log.debug("Auto registered invite {} on guild {}({})", i.getCode(), guild.getName(), guild.getId());
            }
        };
    }
}
