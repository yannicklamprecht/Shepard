package de.eldoria.shepard.scheduler.invites;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.queries.InviteData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;

class RefreshInvites implements Runnable {
    RefreshInvites() {
    }


    @Override
    public void run() {
        JDA jda = ShepardBot.getJDA();

        if (jda == null) {
            return;
        }

        for (Guild guild : ShepardBot.getJDA().getGuilds()) {
            if (!guild.getMember(ShepardBot.getJDA().getSelfUser()).hasPermission(Permission.MANAGE_SERVER)) {
                continue;
            }
            if (InviteData.updateInvite(guild, guild.retrieveInvites().complete(), null)) {
                ShepardBot.getLogger().info("Update Invites for guild " + guild.getName()
                        + "(" + guild.getId() + ")");
            }
        }
        ShepardBot.getLogger().info("Cleaned up Invites");
    }
}
