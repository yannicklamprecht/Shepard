package de.eldoria.shepard.listener;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

class GroupListener extends ListenerAdapter {

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        if (!event.getGuild().getId().equalsIgnoreCase("214352508594814976")) return;
        //TODO: if (userIsInTeamDatabase && !userHasGroup(Team) -> Remove from Team database & add to changelog
        //TODO: if (!userIsInTeamDatabase && userHasGroup(Team) -> Add to Team database & add to changelog
    }
}
