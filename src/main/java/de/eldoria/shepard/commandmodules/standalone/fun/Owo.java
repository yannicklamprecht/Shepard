package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.fun.OwoLocale;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.EventWrapper;

/**
 * Command which sends a owo to the chat with large emoji letters.
 */
public class Owo extends Command implements Executable {

    /**
     * Creates a new owo keyword object.
     */
    public Owo() {
        super("owo",
                new String[] {"owod"},
                OwoLocale.DESCRIPTION.tag,
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        MessageSender.sendMessage(":regional_indicator_o::regional_indicator_w::regional_indicator_o:",
                wrapper.getMessageChannel());

        if (label.equalsIgnoreCase("owod")) {
            wrapper.getMessage().get().delete().queue();
        }

    }
}
