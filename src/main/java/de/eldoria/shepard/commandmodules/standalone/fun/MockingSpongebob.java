package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.FileHelper;
import de.eldoria.shepard.wrapper.EventWrapper;

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
                SubCommand.builder("mockingSpongebob")
                        .addSubcommand(DESCRIPTION.tag, Parameter.createInput(GeneralLocale.A_TEXT.tag, null, true))
                        .build(),
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        if (args.length == 0) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, wrapper);
            return;
        }
        File image = FileHelper.getFileFromURL(BASE_URL + String.join("%20", args) + ".jpg");
        if (image == null) {
            MessageSender.sendSimpleError(ErrorType.SERVICE_UNAVAILABLE, wrapper);
        } else {
            wrapper.getMessageChannel().sendFile(image).queue();
        }
    }
}
