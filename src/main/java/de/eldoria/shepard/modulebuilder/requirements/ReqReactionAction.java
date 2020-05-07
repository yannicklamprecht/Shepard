package de.eldoria.shepard.modulebuilder.requirements;

import de.eldoria.shepard.basemodules.reactionactions.ReactionActionCollection;

public interface ReqReactionAction {
    /**
     * Add a {@link ReactionActionCollection} to the object.
     *
     * @param reactionAction reaction action instance
     */
    void addReactionAction(ReactionActionCollection reactionAction);
}
