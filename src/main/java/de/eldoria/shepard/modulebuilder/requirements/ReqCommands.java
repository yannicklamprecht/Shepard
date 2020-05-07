package de.eldoria.shepard.modulebuilder.requirements;

import de.eldoria.shepard.basemodules.commanddispatching.CommandHub;

public interface ReqCommands {
    /**
     * Add the command hub to a object.
     * @param commandHub command hub instance
     */
    void addCommands(CommandHub commandHub);

}
