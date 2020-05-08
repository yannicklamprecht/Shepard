package de.eldoria.shepard.commandmodules.greeting.listener;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
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
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
        GreetingSettings greeting = greetingData.getGreeting(event.getGuild());

        if (greeting == null) return;

        Optional<TextChannel> textChannel = ArgumentParser.getTextChannel(event.getGuild(),
                greeting.getChannel().getId());

        if (textChannel.isEmpty()) return;

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

        if (diffInvites.isEmpty()){
            //If no invite was found.
            MessageSender.sendGreeting(event, greeting, null, textChannel.get());
            return;
        }

        DatabaseInvite databaseInvite = diffInvites.get(0);

        MessageSender.sendGreeting(event, greeting, databaseInvite.getSource(), textChannel.get());

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
