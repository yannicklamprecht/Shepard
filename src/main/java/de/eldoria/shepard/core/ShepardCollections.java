package de.eldoria.shepard.core;

import de.eldoria.shepard.basemodules.reactionactions.ReactionActionCollection;
import de.eldoria.shepard.commandmodules.privatemessages.PrivateMessageCollection;
import de.eldoria.shepard.commandmodules.repeatcommand.LatestCommandsCollection;
import de.eldoria.shepard.core.util.Normandy;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import lombok.Getter;
import net.dv8tion.jda.api.sharding.ShardManager;

/**
 * Collection if essential core collections.
 * Contains readonly collections.
 */
@Getter
public class ShepardCollections implements ReqShardManager, ReqInit {

    private LatestCommandsCollection latestCommands;
    private PrivateMessageCollection privateMessages;
    private ReactionActionCollection reactionActions;
    private Normandy normandy;
    private ShardManager shardManager;

    /**
     * Create a new shepard collection.
     * All objects are created within the object.
     */
    public ShepardCollections() {
    }

    @Override
    public void init() {
        latestCommands = new LatestCommandsCollection();
        privateMessages = new PrivateMessageCollection();
        reactionActions = new ReactionActionCollection();
        normandy = new Normandy();
        normandy.addShardManager(shardManager);
    }

    @Override
    public void addShardManager(ShardManager shardManager) {
        this.shardManager = shardManager;
    }
}
