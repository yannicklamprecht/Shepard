package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.awt.*;

public class Home extends Command {
    /**
     * Creates a new gome command object.
     */
    public Home() {
        commandName = "home";
        commandDesc = "Join my developers and me on our own discord an test my beta features!";
        commandAliases = new String[] {"normandy"};
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        MessageSender.sendSimpleTextBox("Come on board of the Normandy SR2",
                "http://discord.shepardbot.de",
                Color.green,
                ShepardReactions.CAT,
                messageContext.getChannel());
    }
}
