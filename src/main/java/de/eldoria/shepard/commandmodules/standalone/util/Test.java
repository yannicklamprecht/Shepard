package de.eldoria.shepard.commandmodules.standalone.util;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

/**
 * A test command without specified behaviour.
 */
public class Test extends Command implements ExecutableAsync {

    /**
     * Creates a new test command.
     */
    public Test() {
        super("test",
                null,
                "Testcommand!",
                CommandCategory.EXCLUSIVE);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        messageContext.getTextChannel().sendMessage("1").queue();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        messageContext.getTextChannel().sendMessage("Wait").queue();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        messageContext.getTextChannel().sendMessage("2").queue();
    }
}
