package de.eldoria.shepard.commandmodules.commandsettings.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.commandsettings.data.CommandData;
import de.eldoria.shepard.commandmodules.commandsettings.types.CommandSettings;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;

import javax.sql.DataSource;
import java.util.Optional;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_COMMAND_NAME;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_COMMAND_NAME;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ContextInfoLocale.DESCRIPTION;
import static java.lang.System.lineSeparator;

/**
 * Gives information about the settings of a registered and active {@link Command}.
 */
@CommandUsage(EventContext.GUILD)
public class CommandInfo extends Command implements Executable, ReqParser, ReqDataSource {

    private ArgumentParser parser;
    private CommandData commandData;

    /**
     * Creates new context info command object.
     */
    public CommandInfo() {
        super("commandInfo",
                new String[] {"cinfo"},
                DESCRIPTION.tag,
                SubCommand.builder("commandInfo")
                        .addSubcommand(null,
                                Parameter.createInput(A_COMMAND_NAME.tag, AD_COMMAND_NAME.tag, true))
                        .build(),
                CommandCategory.BOT_CONFIG);
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        Optional<Command> command = parser.getCommand(args[0]);
        if (command.isPresent()) {
            CommandSettings data = commandData.getCommandData(command.get(), wrapper);

            MessageSender.sendMessage("Information about command " + command.get().getCommandName().toUpperCase()
                    + lineSeparator()
                    + "```yaml" + lineSeparator()
                    + data.buildString(wrapper.getJDA()) + lineSeparator() + "```", wrapper.getMessageChannel());
        } else {
            MessageSender.sendSimpleError(ErrorType.INVALID_CONTEXT, wrapper);
        }
    }

    @Override
    public void addDataSource(DataSource source) {
        commandData = new CommandData(source);
    }
}
