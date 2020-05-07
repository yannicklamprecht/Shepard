package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.util.Collections;
import java.util.Random;

import static de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale.ANSWER_NEGATIVE;
import static de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale.ANSWER_NEUTRAL;
import static de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale.ANSWER_POSITIVE;
import static de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale.M_ANSWER;


/**
 * Command which provides magic conch sentences.
 */
public class MagicConch extends Command implements Executable {
    /**
     * Creates a new MagicConch command object.
     */
    public MagicConch() {
        super("magicConch",
                new String[] {"conch"},
                DESCRIPTION.tag,
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String word;
        Random rand = new Random();
        int type = rand.nextInt(3);


        MagicConchLocale answer;
        switch (type) {
            case 0:
                answer = ANSWER_POSITIVE;
                break;
            case 1:
                answer = ANSWER_NEGATIVE;
                break;
            case 2:
                answer = ANSWER_NEUTRAL;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        String[] decisions = locale.getLanguageString(messageContext.getGuild(), answer.localeCode).split("\\|");
        word = decisions[rand.nextInt(decisions.length)];

        MessageSender.sendTextBox(null,
                Collections.singletonList(new LocalizedField(M_ANSWER.tag, word, false, messageContext)),
                messageContext.getTextChannel());

    }
}
