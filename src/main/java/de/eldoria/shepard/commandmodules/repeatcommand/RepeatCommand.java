package de.eldoria.shepard.commandmodules.repeatcommand;

import de.eldoria.shepard.basemodules.commanddispatching.CommandHub;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqCommands;
import de.eldoria.shepard.modulebuilder.requirements.ReqLatestCommands;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import static de.eldoria.shepard.localization.enums.commands.admin.RepeatCommandLocale.DESCRIPTION;

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
                new String[] {"repeat", "rc"},
                DESCRIPTION.tag,
                null,
                "",
                CommandCategory.ADMIN);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        LatestCommandsCollection.SavedCommand latestCommand = latestCommands
                .getLatestCommand(messageContext.getGuild(), messageContext.getAuthor());

        if (latestCommand == null) {
            MessageSender.sendSimpleError(ErrorType.NO_LAST_COMMAND_FOUND, messageContext.getTextChannel());
            return;
        }

        if (latestCommand.getCommand().getCommandName().equals(this.getCommandName())) {
            return;
        }
        commands.runCommand(
                latestCommand.getCommand(), latestCommand.getLabel(), latestCommand.getArgs(), messageContext);
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
