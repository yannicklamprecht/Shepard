package de.eldoria.shepard.util;

import de.eldoria.shepard.collections.ReactionActionCollection;
import de.eldoria.shepard.reactionactions.Action;
import de.eldoria.shepard.util.UniqueMessageIdentifier;

public class ActionRemover implements Runnable {
    private final UniqueMessageIdentifier umi;
    private final Action action;

    /**
     * Creates a new action remover.
     *
     * @param umi    umi to identify the message.
     * @param action action to remove
     */
    public ActionRemover(UniqueMessageIdentifier umi, Action action) {
        this.umi = umi;
        this.action = action;
    }

    @Override
    public void run() {
        ReactionActionCollection.getInstance().removeAction(umi, action);
    }
}
