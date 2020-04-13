package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import static de.eldoria.shepard.localization.enums.commands.fun.UwuLocale.DESCRIPTION;

/**
 * Command which sends a uwu to the chat with large emoji letters.
 */
public class Uwu extends Command {

    /**
     * Creates new uwu command object.
     */
    public Uwu() {
        super("uwu",
                new String[] {"uwud"},
                DESCRIPTION.tag,
                ContextCategory.FUN);
    }


    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        MessageSender.sendMessage(":regional_indicator_u::regional_indicator_w::regional_indicator_u:",
                messageContext.getTextChannel());

        if (label.equalsIgnoreCase("uwud")) {
            messageContext.getMessage().delete().queue();
        }
    }
}
