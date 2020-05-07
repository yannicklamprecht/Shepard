package de.eldoria.shepard.modulebuilder.requirements;

import de.eldoria.shepard.commandmodules.repeatcommand.LatestCommandsCollection;

public interface ReqLatestCommands {
    /**
     * Add a latest command collection to the object.
     *
     * @param latestCommands latest command instance
     */
    void addLatestCommand(LatestCommandsCollection latestCommands);
}
