package de.eldoria.shepard.listener;

import de.eldoria.shepard.database.queries.GreetingData;
import de.eldoria.shepard.database.queries.InviteData;
import de.eldoria.shepard.database.types.DatabaseInvite;
import de.eldoria.shepard.database.types.GreetingSettings;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.SQLException;
import java.util.List;

public class GreetingListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        List<Invite> invites = event.getGuild().retrieveInvites().complete();
        List<DatabaseInvite> databaseInvites;
        try {
            databaseInvites = InviteData.getInvites(event.getGuild(), null);
        } catch (SQLException e) {
            return;
        }

        GreetingSettings greeting;
        try {
            greeting = GreetingData.getGreeting(event.getGuild());
        } catch (SQLException e) {
            return;
        }

        if (greeting == null) return;
        MessageChannel channel = greeting.getChannel();
        if (channel == null) return;
        for (Invite invite : invites) {
            var dInvite = databaseInvites.stream()
                    .filter(inv -> inv.getCode().equals(invite.getCode())).findAny();
            if (dInvite.isEmpty()) continue;
            if (invite.getUses() != dInvite.get().getUsedCount()) {
                try {
                    InviteData.upcountInvite(event.getGuild(), invite.getCode(), null);
                } catch (SQLException e) {
                    return;
                }

                MessageSender.sendGreeting(event, greeting, dInvite.get().getSource(), channel);
            }
        }
        MessageSender.sendGreeting(event, greeting, null, channel);
    }
}
