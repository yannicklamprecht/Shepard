package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;

public class Owod extends Command {

    /**
     * Creates a new owod keyword.
     */
    public Owod() {
        commandName = "owod";
        commandDesc = "OWO and delete";
    }


    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        MessageSender.sendMessage(":regional_indicator_o::regional_indicator_w::regional_indicator_o:",
                messageContext.getChannel());
        MessageSender.deleteMessage(messageContext);
    }
}
