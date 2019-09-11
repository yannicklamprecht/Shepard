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

import java.util.List;
import java.util.Optional;

public class GreetingListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        List<Invite> invites = event.getGuild().retrieveInvites().complete();
        List<DatabaseInvite> databaseInvites;
        databaseInvites = InviteData.getInvites(event.getGuild(), null);

        GreetingSettings greeting;
        greeting = GreetingData.getGreeting(event.getGuild());

        if (greeting == null) return;
        MessageChannel channel = greeting.getChannel();
        if (channel == null) return;
        for (Invite invite : invites) {
            Optional<DatabaseInvite> dInvite = databaseInvites.stream()
                    .filter(inv -> inv.getCode().equals(invite.getCode())).findAny();
            if (dInvite.isEmpty()) continue;
            if (invite.getUses() != dInvite.get().getUsedCount()) {
                for (int i = dInvite.get().getUsedCount(); i < invite.getUses(); i++) {
                    InviteData.upCountInvite(event.getGuild(), invite.getCode(), null);
                }

                MessageSender.sendGreeting(event, greeting, dInvite.get().getSource(), channel);
            }
        }
        MessageSender.sendGreeting(event, greeting, null, channel);
    }
}
