package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.localization.enums.commands.util.HomeLocale;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.awt.Color;

/**
 * Command which provides a invite link to normandy.
 */
public class Home extends Command {
    /**
     * Creates a new home command object.
     */
    public Home() {
        commandName = "home";
        commandDesc = HomeLocale.DESCRIPTION.tag;
        commandAliases = new String[] {"normandy"};
        category = ContextCategory.UTIL;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        MessageSender.sendSimpleTextBox(HomeLocale.M_COME_ON_BOARD.tag,
                "[" + HomeLocale.M_JOIN_NOW + "](https://discord.gg/AJyFGAj)",
                Color.green, ShepardReactions.CAT, messageContext.getTextChannel());
    }
}
