package de.eldoria.shepard.listener;

import de.eldoria.shepard.util.Verifier;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

import java.util.List;

import static de.eldoria.shepard.database.queries.TicketData.getChannelIdsByOwner;
import static de.eldoria.shepard.database.queries.TicketData.removeChannel;

public class TicketCleanupListener extends ListenerAdapter {
    @Override
    public void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent event) {
        List<String> channelIds = getChannelIdsByOwner(event.getGuild(), event.getUser(), null);

        List<TextChannel> validTextChannels = Verifier.getValidTextChannels(event.getGuild(), channelIds);
        for (TextChannel channel : validTextChannels) {
            removeChannel(event.getGuild(), channel, null);
            channel.delete().queue();
        }

    }
}
