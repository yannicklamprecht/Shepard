package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.configuration.Config;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.ExitCode;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

/**
 * Command to restart the bot. Only usable on a bot, where {@link Config#isBeta()} is not true.
 */
public class Restart extends Command {

    /**
     * Creates a restart command.
     */
    public Restart() {
        super("restart",
                null,
                "",
                null,
                ContextCategory.BOT_CONFIG);
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
