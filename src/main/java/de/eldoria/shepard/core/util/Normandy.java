package de.eldoria.shepard.core.util;

import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.sharding.ShardManager;

/**
 * The normandy provides several static channel for logging.
 * Should be removed in future update and replaced by webhooks.
 */
public class Normandy implements ReqShardManager {
    private ShardManager jda;

    /**
     * Get the private answer channel.
     *
     * @return private answer channel
     */
    public TextChannel getPrivateAnswerChannel() {
        long privateAnswerChannel = 627891573430157334L;
        long normandy = 538084337984208906L;
        return jda.getGuildById(normandy).getTextChannelById(privateAnswerChannel);
    }

    @Override
    public void addShardManager(ShardManager shardManager) {
        this.jda = shardManager;
    }
}
