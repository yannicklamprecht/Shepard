package de.eldoria.shepard.commandmodules.standalone.botconfig;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqShepard;
import de.eldoria.shepard.util.ExitCode;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Message;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Command which downloads a send file and replaces the current ShepardBot.jar
 * File must be a jar. Use with extreme caution.
 * TODO: Check filesize.  If the file size is much larger or smaller than the current file deny the upgrade process.
 */
public class Upgrade extends Command implements ExecutableAsync, ReqConfig, ReqShepard {

    private Config config;
    private ShepardBot bot;

    /**
     * Creates a new Upgrade Command.
     */
    public Upgrade() {
        super("upgrade",
                null,
                "",
                null,
                "",
                CommandCategory.BOT_CONFIG);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        if (config.getGeneralSettings().isBeta()) {
            MessageSender.sendMessage("Only on main bot!", wrapper.getMessageChannel());
            return;
        }

        File shepardJar = new File(".");
        File shepardFolder = shepardJar.getAbsoluteFile().getParentFile();
        String home = shepardFolder.toString();
        File jar = new File(home + "/Shepard.jar");

        if (!jar.exists()) {
            MessageSender.sendMessage("Couldn't find a old Version of myself. Thats weird.",
                    wrapper.getMessageChannel());
        }

        if (wrapper.getMessage().get().getAttachments().isEmpty()) {
            MessageSender.sendMessage("Please provide a upgrade jar!", wrapper.getMessageChannel());
            return;
        }

        Message.Attachment attachment = wrapper.getMessage().get().getAttachments().get(0);

        MessageSender.sendMessage("Checking file!", wrapper.getMessageChannel());
        if (!attachment.getFileExtension().equalsIgnoreCase("jar")) {
            MessageSender.sendMessage("Please provide a upgrade jar!", wrapper.getMessageChannel());
            return;
        }
        MessageSender.sendMessage("File is jar. Deleting old jar.", wrapper.getMessageChannel());
        boolean delete = jar.delete();
        if (!delete) {
            MessageSender.sendMessage("Couldn't delete File.", wrapper.getMessageChannel());
        }

        MessageSender.sendMessage("Old Version deleted. Downloading new Version.", wrapper.getMessageChannel());

        try {
            attachment.downloadToFile(home + "/Shepard.jar").get();
        } catch (ExecutionException | InterruptedException e) {
            return;
        }

        MessageSender.sendMessage("Download completed!" + System.lineSeparator() + "**RESTARTING**",
                wrapper.getMessageChannel());

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
