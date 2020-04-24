package de.eldoria.shepard.commandmodules.standalone.util;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import static de.eldoria.shepard.localization.enums.commands.util.GetRawLocale.DESCRIPTION;

/**
 * A command for echoing messages in a raw format.
 */
public class GetRaw extends Command implements Executable {

    /**
     * Creates a new get raw command object.
     */
    public GetRaw() {
        super("getRaw",
                null,
                DESCRIPTION.tag,
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        MessageSender.sendMessage("`" + ArgumentParser.getMessage(args, 0) + "`",
                messageContext.getTextChannel());
    }
}
