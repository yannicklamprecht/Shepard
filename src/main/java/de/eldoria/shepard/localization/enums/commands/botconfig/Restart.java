package de.eldoria.shepard.localization.enums.commands.botconfig;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.ExitCode;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.security.MessageDigest;

public class Restart extends Command {

    public Restart() {
        commandName = "restart";
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (ShepardBot.getConfig().isBeta()) {
            MessageSender.sendMessage("Only on main bot!", messageContext.getTextChannel());
            return;
        }
        MessageSender.sendMessage("**RESTARTING**", messageContext.getTextChannel());
        ShepardBot.getInstance().shutdown(ExitCode.RESTART);
    }
}
