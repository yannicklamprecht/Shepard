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
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        @Nullable GreetingSettings greeting = greetingData.getGreeting(event.getGuild());

        ImmutableMap<String, Invite> serverInvites = Maps.uniqueIndex(
                event.getGuild().retrieveInvites().complete(), Invite::getCode);

        ImmutableMap<String, DatabaseInvite> databaseInvites = Maps.uniqueIndex(
                inviteData.getInvites(event.getGuild(), null), DatabaseInvite::getCode);

        List<DatabaseInvite> diffInvites = new ArrayList<>();

        // Search for different usage count in invites
        for (Map.Entry<String, DatabaseInvite> entry : databaseInvites.entrySet()) {
            Invite invite = serverInvites.get(entry.getKey());
            if (invite == null) {
                continue;
            }

            if (invite.getUses() == entry.getValue().getUses()) continue;

            diffInvites.add(entry.getValue());
        }

        if (greeting != null && greeting.getPrivateMessage() != null) {
            event.getUser().openPrivateChannel().queue(c -> c.sendMessage(greeting.getPrivateMessage()).queue(), e -> log.error("Could not send greeting message.", e));
        }

        if (greeting != null && greeting.getRole() != null) {
            if (event.getGuild().getSelfMember().canInteract(greeting.getRole())) {
                event.getGuild().addRoleToMember(event.getMember(), greeting.getRole()).queue();
            }
        }

        if (diffInvites.isEmpty() && greeting != null) {
            //If no invite was found.
            MessageSender.sendGreeting(event, greeting, null);
            return;
        }

        DatabaseInvite databaseInvite = diffInvites.get(0);
        if (greeting != null) {
            MessageSender.sendGreeting(event, greeting, databaseInvite.getSource());
        }

        if (databaseInvite.getRole() != null) {
            if (event.getGuild().getSelfMember().canInteract(databaseInvite.getRole())) {
                event.getGuild().addRoleToMember(event.getMember(), databaseInvite.getRole()).queue();
            }
        }

        // Update all invited which differ. Because why not. Better safe than sorry.
        for (DatabaseInvite invite : diffInvites) {
            Invite serverInvite = serverInvites.get(invite.getCode());
            inviteData.addInvite(event.getGuild(), invite.getCode(), invite.getSource(), serverInvite.getUses(), null);
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
