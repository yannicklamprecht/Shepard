package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import static de.eldoria.shepard.localization.enums.commands.fun.UwuLocale.DESCRIPTION;

/**
 * Command which sends a uwu to the chat with large emoji letters.
 */
public class Uwu extends Command implements Executable {

    /**
     * Creates new uwu command object.
     */
    public Uwu() {
        super("uwu",
                new String[] {"uwud"},
                DESCRIPTION.tag,
                CommandCategory.FUN);
    }


    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        MessageSender.sendMessage(":regional_indicator_u::regional_indicator_w::regional_indicator_u:",
                messageContext.getTextChannel());

        if (label.equalsIgnoreCase("uwud")) {
            messageContext.getMessage().delete().queue();
        }
    }
}
