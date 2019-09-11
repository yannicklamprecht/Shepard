package de.eldoria.shepard.scheduler.invites;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.queries.InviteData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.SQLException;
import java.util.List;

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
            try {
                InviteData.updateInvite(guild, guild.retrieveInvites().complete(), null);

            } catch (SQLException e) {
                return;
            }

        }
        ShepardBot.getLogger().info("Cleaned up Invites");
    }
}
