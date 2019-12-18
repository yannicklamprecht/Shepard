package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.ExitCode;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

public class Restart extends Command {

    public Restart() {
        commandName = "restart";
        category = ContextCategory.BOT_CONFIG;
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
