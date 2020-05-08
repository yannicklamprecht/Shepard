package de.eldoria.shepard.commandmodules.greeting.listener;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.greeting.data.GreetingData;
import de.eldoria.shepard.commandmodules.greeting.data.InviteData;
import de.eldoria.shepard.commandmodules.greeting.types.DatabaseInvite;
import de.eldoria.shepard.commandmodules.greeting.types.GreetingSettings;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.List;
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

        List<Invite> serverInvites = event.getGuild().retrieveInvites().complete();

        List<DatabaseInvite> databaseInvites = inviteData.getInvites(event.getGuild(), null);

        for (Invite sInvite : serverInvites) {
            for (DatabaseInvite dInvite : databaseInvites) {
                if (sInvite.getUses() != dInvite.getUsedCount()) {
                    for (int i = dInvite.getUsedCount(); i < sInvite.getUses(); i++) {
                        inviteData.upCountInvite(event.getGuild(), sInvite.getCode(), null);
                    }
                    MessageSender.sendGreeting(event, greeting, dInvite.getSource(), textChannel.get());
                    return;
                }
            }
        }

        //If no invite was found.
        MessageSender.sendGreeting(event, greeting, null, textChannel.get());
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
