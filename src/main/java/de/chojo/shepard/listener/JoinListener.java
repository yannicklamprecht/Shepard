package de.chojo.shepard.listener;

import de.chojo.shepard.database.queries.Greetings;
import de.chojo.shepard.database.queries.Invites;
import de.chojo.shepard.database.types.DatabaseInvite;
import de.chojo.shepard.database.types.Greeting;
import de.chojo.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class JoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        List<Invite> invites = event.getGuild().retrieveInvites().complete();
        List<DatabaseInvite> databaseInvites = Invites.getInvites(event.getGuild(), null);

        Greeting greeting = Greetings.getGreeting(event.getGuild());
        if (greeting == null) return;
        MessageChannel channel = greeting.getChannel();
        if (channel == null) return;
        for (Invite invite : invites) {
            var dInvite = databaseInvites.stream()
                    .filter(inv -> inv.getCode().equals(invite.getCode())).findAny();
            if (dInvite.isEmpty()) continue;
            if (invite.getUses() != dInvite.get().getUsedCount()) {
                Invites.upcountInvite(event.getGuild(), invite.getCode(), null);
                MessageSender.sendGreeting(event, greeting, dInvite.get().getSource(), channel);
            }
        }
        MessageSender.sendGreeting(event, greeting, null, channel);
    }
}
