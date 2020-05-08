package de.eldoria.shepard.commandmodules.ticketsystem.listener;

import de.eldoria.shepard.commandmodules.ticketsystem.data.TicketData;
import de.eldoria.shepard.core.Statistics;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqStatistics;
import de.eldoria.shepard.util.Verifier;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.List;

public class TicketCleanupListener extends ListenerAdapter implements ReqInit, ReqDataSource, ReqStatistics {
    private DataSource source;
    private TicketData ticketData;
    private Statistics statistics;

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        List<String> channelIds = ticketData.getChannelIdsByOwner(event.getGuild(), event.getUser(), null);

        statistics.eventDispatched(event.getJDA());

        List<TextChannel> validTextChannels = Verifier.getValidTextChannels(event.getGuild(), channelIds);
        for (TextChannel channel : validTextChannels) {
            if (ticketData.removeChannel(event.getGuild(), channel, null)) {
                channel.delete().queue();
            }
        }
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    @Override
    public void init() {
        ticketData = new TicketData(source);
    }

    @Override
    public void addStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}
