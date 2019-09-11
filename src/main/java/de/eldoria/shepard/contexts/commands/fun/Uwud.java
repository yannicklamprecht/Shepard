package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.listener.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;

public class Uwud extends Command {

    /**
     * Creates new uwud command object.
     */
    public Uwud() {
        commandName = "uwud";
        commandDesc = "UWU and delete";
    }


    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper dataWrapper) {
        MessageSender.sendMessage(":regional_indicator_u::regional_indicator_w::regional_indicator_u:",
                dataWrapper.getChannel());
        MessageSender.deleteMessage(dataWrapper);
    }
}
