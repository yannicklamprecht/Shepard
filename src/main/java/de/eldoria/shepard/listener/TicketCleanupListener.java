package de.eldoria.shepard.listener;

import de.eldoria.shepard.util.Verifier;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

import java.sql.SQLException;
import java.util.List;

import static de.eldoria.shepard.database.queries.TicketData.getChannelIdsByOwner;
import static de.eldoria.shepard.database.queries.TicketData.removeChannel;

public class TicketCleanupListener extends ListenerAdapter {
    @Override
    public void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent event) {
        List<String> channelIds;
        try {
            channelIds = getChannelIdsByOwner(event.getGuild(), event.getUser(), null);
        } catch (SQLException e) {
            return;
        }


        List<TextChannel> validTextChannels = Verifier.getValidTextChannels(event.getGuild(), channelIds);
        for (TextChannel channel : validTextChannels) {
            try {
                removeChannel(event.getGuild(), channel, null);
            } catch (SQLException e) {
                return;
            }

            channel.delete().queue();
        }

    }
}
