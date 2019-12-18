package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.localization.enums.commands.util.HireMeLocale;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.util.Collections;

/**
 * A command for retrieving a invite link for this bot.
 */
public class HireMe extends Command {

    /**
     * Creates new Hire me object.
     */
    public HireMe() {
        commandName = "hireMe";
        commandAliases = new String[] {"Iwantyou"};
        commandDesc = HireMeLocale.DESCRIPTION.tag;
        category = ContextCategory.UTIL;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (label.equalsIgnoreCase(commandName)) {
            MessageSender.sendTextBox(null, Collections.singletonList(new LocalizedField(HireMeLocale.M_HIRE_ME.tag,
                    "[" + HireMeLocale.M_TAKE_ME + "](http://bit.ly/shepardbot)",
                    false, messageContext)), messageContext.getTextChannel());
        } else {
            MessageSender.sendTextBox(null, Collections.singletonList(new LocalizedField(HireMeLocale.M_I_WANT_YOU.tag,
                    "[" + HireMeLocale.M_TAKE_ME + "](http://bit.ly/shepardbot)",
                    false, messageContext)), messageContext.getTextChannel());
        }
    }
}
