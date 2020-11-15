package de.eldoria.shepard.commandmodules.greeting.listener;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import de.eldoria.shepard.commandmodules.greeting.data.GreetingData;
import de.eldoria.shepard.commandmodules.greeting.data.InviteData;
import de.eldoria.shepard.commandmodules.greeting.types.DatabaseInvite;
import de.eldoria.shepard.commandmodules.greeting.types.GreetingSettings;
import de.eldoria.shepard.core.Statistics;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import de.eldoria.shepard.modulebuilder.requirements.ReqStatistics;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.audit.AuditLogKey;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class GreetingListener extends ListenerAdapter implements ReqShardManager, ReqDataSource, ReqStatistics, ReqInit {

    private GreetingData greetingData;
    private InviteData inviteData;
    private DataSource source;
    private ShardManager shardManager;
    private Statistics statistics;

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        statistics.eventDispatched(event.getJDA());
        CompletableFuture.runAsync(() -> handleGreeting(event));
    }

    private void handleGreeting(GuildMemberJoinEvent event) {
        User user = event.getUser();
        Guild guild = event.getGuild();

        @Nullable GreetingSettings greeting = greetingData.getGreeting(guild);

        Pair<Optional<DatabaseInvite>, Optional<Invite>> searchResult = searchAndUpdateInvite(guild);

        Optional<DatabaseInvite> databaseInvite = searchResult.getLeft();
        Optional<Invite> serverInvite = searchResult.getRight();

        sendGreeting(user, databaseInvite, greeting);
        sendPrivateGreeting(user, greeting);

        databaseInvite.ifPresent(i -> addInviteRoles(user, guild, i));

        addJoinRoles(user, guild, greeting);

        // We didnt found a matchin invite in our database which increased dount or was deleted.
        // Check if we can find the source of the invite by searching for leatest.
        if (databaseInvite.isEmpty()) {
            Pair<String, User> unregisteredInvite = findUnregisteredInvite(guild);
            inviteData.logInvite(guild, user, unregisteredInvite.getRight(), null);
        } else {
            // Just register it and we are happy tho.
            inviteData.logInvite(guild, user, databaseInvite.get().getRefer(), databaseInvite.get().getSource());
        }
    }

    private Pair<String, User> findUnregisteredInvite(Guild guild) {
        if (!guild.getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS)) {
            return Pair.of(null, null);
        }

        // Retrieve all registered invites in db
        ImmutableMap<String, DatabaseInvite> databaseInvites = Maps.uniqueIndex(
                inviteData.getInvites(guild, null), DatabaseInvite::getCode);

        // Now we want to search for a fresh invite which was created in the last seconds and was not registered in our database.
        List<AuditLogEntry> result = guild.retrieveAuditLogs().type(ActionType.INVITE_CREATE).complete();
        // Lets iterate a bit.
        for (AuditLogEntry entry : result) {
            String code = entry.getChangeByKey(AuditLogKey.INVITE_CODE).getNewValue();
            if (!databaseInvites.containsKey(code)) {
                // Found it! Hopefully.
                User user = guild.getJDA().getUserById(entry.getChangeByKey(AuditLogKey.INVITE_INVITER).getNewValue());
                return Pair.of(code, user);
            }
        }
        return Pair.of(null, null);
    }

    private Pair<Optional<DatabaseInvite>, Optional<Invite>> searchAndUpdateInvite(Guild guild) {
        ImmutableMap<String, Invite> serverInvites = Maps.uniqueIndex(
                guild.retrieveInvites().complete(), Invite::getCode);

        ImmutableMap<String, DatabaseInvite> databaseInvites = Maps.uniqueIndex(
                inviteData.getInvites(guild, null), DatabaseInvite::getCode);

        List<DatabaseInvite> diffInvites = new ArrayList<>();
        List<DatabaseInvite> deletedInvites = new ArrayList<>();

        // Search for different usage count in invites
        for (Map.Entry<String, DatabaseInvite> entry : databaseInvites.entrySet()) {
            Invite invite = serverInvites.get(entry.getKey());
            if (invite == null) {
                // If the invite is deleted it is most probably a one time invite.
                deletedInvites.add(entry.getValue());
                continue;
            }

            if (invite.getUses() == entry.getValue().getUsedCount()) continue;

            diffInvites.add(entry.getValue());
        }

        // Update all invited which differ. Because why not. Better safe than sorry.
        refreshGuildInvites(guild, diffInvites);

        // Merge both lists. The count diffs take precendence.
        diffInvites.addAll(deletedInvites);


        Optional<DatabaseInvite> databaseInvite = diffInvites.stream().findFirst();
        return Pair.of(databaseInvite, databaseInvite.map(i -> serverInvites.get(i.getCode())));
    }

    private void refreshGuildInvites(Guild guild, List<DatabaseInvite> databaseInvites) {
        ImmutableMap<String, Invite> serverInvites = Maps.uniqueIndex(
                guild.retrieveInvites().complete(), Invite::getCode);

        for (DatabaseInvite invite : databaseInvites) {
            Invite serverInvite = serverInvites.get(invite.getCode());
            inviteData.addInvite(guild, serverInvite.getInviter(), invite.getCode(), invite.getSource(), serverInvite.getUses(), null);
        }

    }

    private void sendGreeting(User user, Optional<DatabaseInvite> diffInvites, GreetingSettings greeting) {
        if (diffInvites.isEmpty() && greeting != null) {
            //If no invite was found.
            MessageSender.sendGreeting(user, greeting, null, greeting.getChannel());
            return;
        }

        if (greeting != null) {
            DatabaseInvite databaseInvite = diffInvites.get();
            MessageSender.sendGreeting(user, greeting, databaseInvite.getSource(), greeting.getChannel());
        }
    }

    private void sendPrivateGreeting(User user, @Nullable GreetingSettings greeting) {
        if (greeting != null && greeting.getPrivateMessage() != null) {
            user.openPrivateChannel().queue(c -> {
                c.sendMessage(greeting.getPrivateMessage()).queue();
            }, e -> {
                log.error("Could not send greeting message.", e);
            });
        }

    }

    private void addInviteRoles(User user, Guild guild, DatabaseInvite invite) {
        if (invite.getRole() != null) {
            if (guild.getSelfMember().canInteract(invite.getRole())) {
                guild.addRoleToMember(user.getIdLong(), invite.getRole()).queue();
            }
        }
    }

    private void addJoinRoles(User user, Guild guild, @Nullable GreetingSettings greeting) {
        if (greeting != null && greeting.getRole() != null) {
            if (guild.getSelfMember().canInteract(greeting.getRole())) {
                guild.addRoleToMember(user.getIdLong(), greeting.getRole()).queue();
            }
        }
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    @Override
    public void addShardManager(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    public void init() {
        greetingData = new GreetingData(shardManager, source);
        inviteData = new InviteData(source);
    }

    @Override
    public void addStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}
