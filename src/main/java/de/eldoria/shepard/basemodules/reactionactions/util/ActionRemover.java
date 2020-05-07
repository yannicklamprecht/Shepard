package de.eldoria.shepard.basemodules.reactionactions.util;

import de.eldoria.shepard.basemodules.reactionactions.ReactionActionCollection;
import de.eldoria.shepard.basemodules.reactionactions.actions.Action;
import de.eldoria.shepard.modulebuilder.requirements.ReqReactionAction;
import de.eldoria.shepard.util.UniqueMessageIdentifier;

public class ActionRemover implements Runnable, ReqReactionAction {
    private final UniqueMessageIdentifier umi;
    private final Action action;
    private ReactionActionCollection reactionAction;

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
        reactionAction.removeAction(umi, action);
    }

    @Override
    public void addReactionAction(ReactionActionCollection reactionAction) {
        this.reactionAction = reactionAction;
    }
}
