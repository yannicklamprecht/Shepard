package de.eldoria.shepard.commandmodules.command;

import de.eldoria.shepard.basemodules.commanddispatching.CommandHub;
import de.eldoria.shepard.wrapper.EventWrapper;

public interface ExecutableAsync {
    /**
     * Method to be executed by {@link CommandHub}.
     * This method will be executed in a separate thread by the {@link CommandHub}
     *  @param label          label of command
     * @param args           arguments of command
     * @param wrapper context of command
     */
    void execute(String label, String[] args, EventWrapper wrapper);
}
