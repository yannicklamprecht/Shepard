package de.eldoria.shepard.commandmodules.commandsettings.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.commandsettings.data.CommandData;
import de.eldoria.shepard.commandmodules.commandsettings.types.CommandSettings;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqJDA;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.JDA;

import javax.sql.DataSource;
import java.util.Optional;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_CONTEXT_NAME;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_CONTEXT_NAME;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ContextInfoLocale.DESCRIPTION;
import static java.lang.System.lineSeparator;

/**
 * Gives information about the settings of a registered and active {@link Command}.
 */
public class CommandInfo extends Command implements Executable, ReqJDA, ReqParser, ReqDataSource {

    private ArgumentParser parser;
    private CommandData commandData;
    private JDA jda;

    /**
     * Creates new context info command object.
     */
    public CommandInfo() {
        super("commandInfo",
                new String[] {"cinfo"},
                DESCRIPTION.tag,
                SubCommand.builder("commandInfo")
                        .addSubcommand(null,
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true))
                        .build(),
                CommandCategory.BOT_CONFIG);
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        Optional<Command> command = parser.getCommand(args[0], messageContext);
        if (command.isPresent()) {
            CommandSettings data = commandData.getCommandData(command.get(), messageContext);

            MessageSender.sendMessage("Information about command " + command.get().getCommandName().toUpperCase()
                    + lineSeparator()
                    + "```yaml" + lineSeparator()
                    + data.buildString(jda) + lineSeparator() + "```", messageContext.getTextChannel());
        } else {
            MessageSender.sendSimpleError(ErrorType.INVALID_CONTEXT, messageContext.getTextChannel());
        }
    }

    @Override
    public void addDataSource(DataSource source) {
        commandData = new CommandData(source);
    }

    @Override
    public void addJDA(JDA jda) {
        this.jda = jda;
    }
}
