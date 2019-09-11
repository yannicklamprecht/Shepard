package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.listener.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;

public class Uwu extends Command {

    /**
     * Creates new uwu command object.
     */
    public Uwu() {
        commandName = "uwu";
        commandDesc = "UWU";
    }


    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper dataWrapper) {
        MessageSender.sendMessage(":regional_indicator_u::regional_indicator_w::regional_indicator_u:",
                dataWrapper.getChannel());
    }
}
