package de.chojo.shepard.listener;

import de.chojo.shepard.database.DatabaseInvite;
import de.chojo.shepard.database.DatabaseQuery;
import de.chojo.shepard.messagehandler.Messages;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.Set;

public class JoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        List<Invite> invites = event.getGuild().retrieveInvites().complete();
        Set<DatabaseInvite> databaseInvites = DatabaseQuery.getInvites(event.getGuild().getId());
        //TODO: compare old and new join count for invites

        //Get Invites from Server DONE
        //Get Invites from Database DONE
        //Compare Server & Database invitecount DONE
        //Write Invite where Counted Up or unknown link DONE
        //Compare Invites on Server and Database HALBDONE
        //Remove unvalid links on Database
        //Save current Invites on database


        for (Invite invite : invites) {
            var dInvite = databaseInvites.stream()
                    .filter(inv -> inv.getCode().equals(invite.getCode())).findAny();
            if (!dInvite.isPresent()) continue;
            if (invite.getUses() != dInvite.get().getUses()) {
                MessageChannel channel = event.getGuild().getTextChannelById(DatabaseQuery.getGreetingChannel(event.getGuild().getId()));
                Messages.sendGreeting(event, dInvite.get().getName(), channel);
                DatabaseQuery.updateInvite(invite);
            }
        }
    }
}
