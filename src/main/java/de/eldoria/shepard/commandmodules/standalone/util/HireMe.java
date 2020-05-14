package de.eldoria.shepard.commandmodules.standalone.util;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.EventWrapper;

import java.util.Collections;

import static de.eldoria.shepard.localization.enums.commands.util.HireMeLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.util.HireMeLocale.M_HIRE_ME;
import static de.eldoria.shepard.localization.enums.commands.util.HireMeLocale.M_I_WANT_YOU;
import static de.eldoria.shepard.localization.enums.commands.util.HireMeLocale.M_TAKE_ME;

/**
 * A command for retrieving a invite link for this bot.
 */
public class HireMe extends Command implements Executable {

    /**
     * Creates new Hire me object.
     */
    public HireMe() {
        super("invite",
                new String[] {"Iwantyou", "hireMe"},
                DESCRIPTION.tag,
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        if (label.equalsIgnoreCase(commandName) || label.equalsIgnoreCase("invite")) {
            MessageSender.sendTextBox(null, Collections.singletonList(new LocalizedField(M_HIRE_ME.tag,
                    "[" + M_TAKE_ME + "](http://bit.ly/shepardbot)",
                    false, wrapper)), wrapper);
        } else {
            MessageSender.sendTextBox(null, Collections.singletonList(new LocalizedField(M_I_WANT_YOU.tag,
                    "[" + M_TAKE_ME + "](http://bit.ly/shepardbot)",
                    false, wrapper)), wrapper);
        }
    }
}
