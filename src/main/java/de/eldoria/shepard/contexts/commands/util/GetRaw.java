package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;

import static de.eldoria.shepard.localization.enums.commands.util.GetRawLocale.DESCRIPTION;

/**
 * A command for echoing messages in a raw format.
 */
public class GetRaw extends Command {

    /**
     * Creates a new get raw command object.
     */
    public GetRaw() {
        commandName = "getRaw";
        commandDesc = DESCRIPTION.tag;
        category = ContextCategory.UTIL;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        MessageSender.sendMessage("`" + messageContext.getMessage().getContentRaw() + "`", messageContext);
    }
}
