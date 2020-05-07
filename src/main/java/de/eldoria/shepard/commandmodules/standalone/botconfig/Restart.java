package de.eldoria.shepard.commandmodules.standalone.botconfig;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.core.configuration.configdata.GeneralSettings;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqShepard;
import de.eldoria.shepard.util.ExitCode;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

/**
 * Command to restart the bot. Only usable on a bot, where {@link GeneralSettings#isBeta()} is not true.
 */
public class Restart extends Command implements Executable, ReqConfig, ReqShepard {

    private Config config;
    private ShepardBot bot;

    /**
     * Creates a restart command.
     */
    public Restart() {
        super("restart",
                null,
                "",
                null,
                "",
                CommandCategory.BOT_CONFIG);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (config.getGeneralSettings().isBeta()) {
            MessageSender.sendMessage("Only on main bot!", messageContext.getTextChannel());
            return;
        }
        MessageSender.sendMessage("**RESTARTING**", messageContext.getTextChannel());
        bot.shutdown(ExitCode.RESTART);
    }

    @Override
    public void addConfig(Config config) {
        this.config = config;
    }

    @Override
    public void addShepard(ShepardBot bot) {
        this.bot = bot;
    }
}
