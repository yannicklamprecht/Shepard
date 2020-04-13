package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import static de.eldoria.shepard.localization.enums.commands.util.SystemInfoLocale.DESCRIPTION;

/**
 * A test command without specified behaviour.
 */
public class Test extends Command {

    /**
     * Creates a new test command.
     */
    public Test() {
        super("test",
                null,
                "Testcommand!",
                ContextCategory.FUN);
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
    }
}
