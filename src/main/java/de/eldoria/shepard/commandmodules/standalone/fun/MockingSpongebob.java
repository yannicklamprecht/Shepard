package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.FileHelper;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.io.File;

import static de.eldoria.shepard.localization.enums.commands.fun.MockingSpongebobLocale.DESCRIPTION;

/**
 * Command which queries a web api to receive a mocking spongenbob image with a specific text
 * Should be executed asynchronous.
 */
public class MockingSpongebob extends Command implements ExecutableAsync {
    private static final String BASE_URL = "https://mockingspongebob.org/";

    /**
     * Creates a new mocking spongebob command object.
     */
    public MockingSpongebob() {
        super("mockingSpongebob",
                new String[] {"msb", "mock"},
                DESCRIPTION.tag,
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 0) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            return;
        }
        File image = FileHelper.getFileFromURL(BASE_URL + String.join("%20", args) + ".jpg");
        if (image == null) {
            MessageSender.sendSimpleError(ErrorType.SERVICE_UNAVAILABLE, messageContext.getTextChannel());
        } else {
            messageContext.getChannel().sendFile(image).queue();
        }
    }
}
