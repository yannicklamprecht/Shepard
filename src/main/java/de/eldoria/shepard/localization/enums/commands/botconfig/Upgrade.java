package de.eldoria.shepard.localization.enums.commands.botconfig;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.ExitCode;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Message;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class Upgrade extends Command {

    public Upgrade() {
        commandName = "upgrade";
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
            MessageSender.sendMessage("Couldn't find a old Version of myself. Thats weird.", messageContext.getTextChannel());
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
