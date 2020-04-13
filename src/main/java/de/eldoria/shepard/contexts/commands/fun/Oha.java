package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.localization.enums.commands.fun.MockingSpongebobLocale;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.util.Random;

import static de.eldoria.shepard.localization.enums.commands.fun.OhaLocale.DESCRIPTION;

/**
 * Provides a command which send a "oha" with a random amount of a's at the end.
 */
public class Oha extends Command {

    /**
     * creates a new oha keyword object.
     */
    public Oha() {
        super("oha",
                new String[] {"ohad"},
                DESCRIPTION.tag,
                ContextCategory.FUN);
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String oha = "oha";
        Random rand = new Random();
        int loops = rand.nextInt(30) + 10;
        oha = oha + "a".repeat(loops);
        MessageSender.sendMessage(oha, messageContext.getTextChannel());

        if (label.equalsIgnoreCase("ohad")) {
            messageContext.getMessage().delete().queue();
        }
    }
}
