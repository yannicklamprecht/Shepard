package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

/**
 * A test command without specified behaviour.
 */
public class Test extends Command {

    /**
     * Creates a new test command.
     */
    public Test() {
        commandName = "test";
        commandDesc = "Testcommand!";
        category = ContextCategory.UTIL;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
    }
}
