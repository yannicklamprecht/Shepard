package de.eldoria.shepard.collections;

import de.eldoria.shepard.reactionactions.Action;

class ActionRemover implements Runnable {
    private final UniqueMessageIdentifier umi;
    private final Action action;

    ActionRemover(UniqueMessageIdentifier umi, Action action) {
        this.umi = umi;
        this.action = action;
    }

    @Override
    public void run() {
        ReactionActionCollection.getInstance().removeAction(umi,action);
    }
}
