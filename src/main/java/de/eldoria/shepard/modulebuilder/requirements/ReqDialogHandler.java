package de.eldoria.shepard.modulebuilder.requirements;

import de.eldoria.shepard.basemodules.commanddispatching.dialogue.DialogHandler;

public interface ReqDialogHandler {
    /**
     * Add a dialogHandler to a object.
     *
     * @param dialogHandler dialogHandler object
     */
    void addDialogHandler(DialogHandler dialogHandler);
}
