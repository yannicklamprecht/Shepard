package de.eldoria.shepard.commandmodules.ticketsystem.listener;

import de.eldoria.shepard.commandmodules.ticketsystem.data.TicketData;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.util.Verifier;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.List;

public class TicketCleanupListener extends ListenerAdapter implements ReqInit, ReqDataSource {
    private DataSource source;
    private TicketData ticketData;

    @Override
    public void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent event) {
        List<String> channelIds = ticketData.getChannelIdsByOwner(event.getGuild(), event.getUser(), null);

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
}
