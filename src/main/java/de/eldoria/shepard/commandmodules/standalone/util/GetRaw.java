package de.eldoria.shepard.commandmodules.standalone.util;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.EventWrapper;

/**
 * A command for echoing messages in a raw format.
 */
public class GetRaw extends Command implements Executable {

    /**
     * Creates a new get raw command object.
     */
    public GetRaw() {
        super("getRaw",
                new String[]{"raw"},
                "command.getRaw.description",
                SubCommand.builder("getRaw").addSubcommand("command.getRaw.description",
                        Parameter.createInput("command.general.argument.message", null, true))
                        .build(),
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        MessageSender.sendMessage("`" + ArgumentParser.getMessage(args, 0) + "`",
                wrapper.getMessageChannel());
    }
}
