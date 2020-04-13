package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.ExitCode;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Message;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Command which downloads a send file and replaces the current ShepardBot.jar
 * File must be a jar. Use with extreme caution.
 * TODO: Check filesize.  If the file size is much larger or smaller than the current file deny the upgrade process.
 */
public class Upgrade extends Command {

    /**
     * Creates a new Upgrade Command.
     */
    public Upgrade() {
        super("upgrade",
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

        File shepardJar = new File(".");
        File shepardFolder = shepardJar.getAbsoluteFile().getParentFile();
        String home = shepardFolder.toString();
        File jar = new File(home + "/Shepard.jar");

        if (!jar.exists()) {
            MessageSender.sendMessage("Couldn't find a old Version of myself. Thats weird.",
                    messageContext.getTextChannel());
        }

        if (messageContext.getMessage().getAttachments().isEmpty()) {
            MessageSender.sendMessage("Please provide a upgrade jar!", messageContext.getTextChannel());
            return;
        }

        Message.Attachment attachment = messageContext.getMessage().getAttachments().get(0);

        MessageSender.sendMessage("Checking file!", messageContext.getTextChannel());
        if (!attachment.getFileExtension().equalsIgnoreCase("jar")) {
            MessageSender.sendMessage("Please provide a upgrade jar!", messageContext.getTextChannel());
            return;
        }
        MessageSender.sendMessage("File is jar. Deleting old jar.", messageContext.getTextChannel());
        boolean delete = jar.delete();
        if (!delete) {
            MessageSender.sendMessage("Couldn't delete File.", messageContext.getTextChannel());
        }

        MessageSender.sendMessage("Old Version deleted. Downloading new Version.", messageContext.getTextChannel());

        try {
            attachment.downloadToFile(home + "/Shepard.jar").get();
        } catch (ExecutionException | InterruptedException e) {
            return;
        }

        MessageSender.sendMessage("Download completed!" + System.lineSeparator() + "**RESTARTING**",
                messageContext.getTextChannel());

        ShepardBot.getInstance().shutdown(ExitCode.RESTART);
    }
}
