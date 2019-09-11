package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.listener.MessageEventDataWrapper;

/**
 * A test command without specified behaviour.
 */
public class Test extends Command {

    public Test() {
        commandName = "test";
        commandDesc = "Testcommand!";
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper dataWrapper) {
    }
}
