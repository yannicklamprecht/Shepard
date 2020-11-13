package de.eldoria.shepard.commandmodules.repeatcommand;

import de.eldoria.shepard.basemodules.commanddispatching.CommandHub;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqCommands;
import de.eldoria.shepard.modulebuilder.requirements.ReqLatestCommands;
import de.eldoria.shepard.wrapper.EventWrapper;

/**
 * Command to execute the last executed command.
 * Uses the {@link LatestCommandsCollection} to find the last used command.
 */
public class RepeatCommand extends Command implements Executable, ReqLatestCommands, ReqCommands {
    private LatestCommandsCollection latestCommands;
    private CommandHub commands;

    /**
     * Creates a new repeat command object.
     */
    public RepeatCommand() {
        super("repeatCommand",
                new String[]{"repeat", "rc"},
                "command.repeatCommand.description",
                null,
                "",
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        LatestCommandsCollection.SavedCommand latestCommand = latestCommands
                .getLatestCommand(wrapper);

        if (latestCommand == null) {
            MessageSender.sendSimpleError(ErrorType.NO_LAST_COMMAND_FOUND, wrapper);
            return;
        }

        commands.dispatchCommand(
                latestCommand.getCommand(), latestCommand.getLabel(), latestCommand.getArgs(), wrapper);
    }

    @Override
    public void addLatestCommand(LatestCommandsCollection latestCommands) {
        this.latestCommands = latestCommands;
    }

    @Override
    public void addCommands(CommandHub commandHub) {
        this.commands = commandHub;
    }
}
