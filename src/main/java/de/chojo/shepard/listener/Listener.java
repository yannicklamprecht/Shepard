package de.chojo.shepard.listener;

import de.chojo.shepard.database.DatabaseConnector;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class Listener extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        List<Invite> invites = event.getGuild().getInvites().complete();
        //TODO: compare old and new join count for invites

        //Get Invites from Server
        //Get Invites from Database
        //Compare Server & Database invitecount
        //Write Invite where Counted Up or unknown link
        //Compare Invites on Server and Database
        //Remove unvalid links on Database
        //Save current Invites on database

        DatabaseConnector.getInvites(event.getGuild().getId());

        for (Invite invite : invites) {
            invite.getUses();


        }
    }


}
