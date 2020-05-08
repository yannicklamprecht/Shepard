package de.eldoria.shepard.modulebuilder.requirements;

import net.dv8tion.jda.api.sharding.ShardManager;

public interface ReqShardManager {
    /**
     * Add a {@link ShardManager} instance to the object.
     * @param shardManager shardManager instance
     */
    void addShardManager(ShardManager shardManager);
}
