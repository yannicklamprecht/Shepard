package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.EventWrapper;

import java.util.Random;

import static de.eldoria.shepard.localization.enums.commands.fun.OhaLocale.DESCRIPTION;

/**
 * Provides a command which send a "oha" with a random amount of a's at the end.
 */
public class Oha extends Command implements Executable {

    /**
     * creates a new oha keyword object.
     */
    public Oha() {
        super("oha",
                new String[] {"ohad"},
                "command.oha.description",
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String oha = "oha";
        Random rand = new Random();
        int loops = rand.nextInt(30) + 10;
        oha = oha + "a".repeat(loops);
        MessageSender.sendMessage(oha, wrapper.getMessageChannel());

        if (label.equalsIgnoreCase("ohad")) {
            wrapper.getMessage().get().delete().queue();
        }
    }
}
