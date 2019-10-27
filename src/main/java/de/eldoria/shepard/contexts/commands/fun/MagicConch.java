package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;

import java.util.Collections;
import java.util.Random;

import static de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale.ANSWER_NEGATIVE;
import static de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale.ANSWER_NEUTRAL;
import static de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale.ANSWER_POSITIVE;
import static de.eldoria.shepard.localization.enums.commands.fun.MagicConchLocale.M_ANSWER;


public class MagicConch extends Command {
    /**
     * Creates a new MagicConch command object.
     */
    public MagicConch() {
        commandName = "magicConch";
        commandAliases = new String[] {"MagischeMiesmuschel"};
        commandDesc = "Find your decision!";
        category = ContextCategory.FUN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
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
                Collections.singletonList(new LocalizedField(M_ANSWER.replacement, word, false, messageContext)),
                messageContext);

    }
}
