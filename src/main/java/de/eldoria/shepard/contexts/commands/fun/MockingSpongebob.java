package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.FileHelper;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.io.File;
import java.util.UUID;

public class MockingSpongebob extends Command {
    private static final String BASE_URL = "https://mockingspongebob.org/";

    public MockingSpongebob() {
        this.commandName = "mockingSpongebob";
        this.commandDesc = "MoCkInG SpOnGeBoB";
        this.commandAliases = new String[] {"msb", "mock"};
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 0) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getChannel());
            return;
        }
        File image = FileHelper.getFileFromURL(BASE_URL + String.join("%20", args) + ".jpg");
        if (image == null) {
            MessageSender.sendSimpleError(ErrorType.SERVICE_UNAVAILABLE, messageContext.getChannel());
        } else {
            messageContext.getChannel().sendFile(image).queue();
        }
    }
}
