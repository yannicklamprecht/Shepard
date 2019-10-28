package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;

import static de.eldoria.shepard.localization.enums.commands.fun.OwoLocale.DESCRIPTION;

public class Owo extends Command {

    /**
     * Creates a new owo keyword object.
     */
    public Owo() {
        commandName = "owo";
        commandDesc = DESCRIPTION.tag;
        commandAliases = new String[]{"owod"};
        category = ContextCategory.FUN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        MessageSender.sendMessage(":regional_indicator_o::regional_indicator_w::regional_indicator_o:",
                messageContext.getTextChannel());

        if (label.equalsIgnoreCase("owod")) {
            messageContext.getMessage().delete().queue();
        }

    }
}
