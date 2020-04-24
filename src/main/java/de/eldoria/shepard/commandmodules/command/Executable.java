package de.eldoria.shepard.commandmodules.command;

import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

public interface Executable {

    /**
     * Method to be executed by {@link de.eldoria.shepard.basemodules.commanddispatching.CommandHub}.
     *
     * @param label          label of command
     * @param args           arguments of command
     * @param messageContext context of command
     */
    void execute(String label, String[] args, MessageEventDataWrapper messageContext);
}
