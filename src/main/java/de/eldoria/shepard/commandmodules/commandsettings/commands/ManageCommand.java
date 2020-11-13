package de.eldoria.shepard.commandmodules.commandsettings.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.commandsettings.data.CommandData;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.OptionalInt;

import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale.*;

/**
 * Manage the basic settings of a registered and active {@link Command}.
 */
@CommandUsage(EventContext.GUILD)
public class ManageCommand extends Command implements Executable, ReqParser, ReqDataSource {

    private ArgumentParser parser;
    private CommandData commandData;

    /**
     * Creates a new manage context command object.
     */
    public ManageCommand() {
        super("manageCommand",
                new String[]{"mc"},
                "command.manageContext.description",
                SubCommand.builder("manageCommand")
                        .addSubcommand("command.manageContext.subcommand.setNsfw",
                                Parameter.createCommand("setNsfw"),
                                Parameter.createInput("command.general.argument.contextName", "command.general.argumentDescription.contextName", true),
                                Parameter.createInput("command.general.argument.boolean", null, true))
                        .addSubcommand("command.manageContext.subcommand.setAdminOnly",
                                Parameter.createCommand("setAdminOnly"),
                                Parameter.createInput("command.general.argument.contextName", "command.general.argumentDescription.contextName", true),
                                Parameter.createInput("command.general.argument.boolean", null, true))
                        .addSubcommand("command.manageContext.subcommand.setUserCooldown",
                                Parameter.createCommand("setUserCooldown"),
                                Parameter.createInput("command.general.argument.contextName", "command.general.argumentDescription.contextName", true),
                                Parameter.createInput("command.general.argument.seconds", "command.general.argumentDescription.seconds", true))
                        .addSubcommand("command.manageContext.subcommand.setGuildCooldown",
                                Parameter.createCommand("setGuildCooldown"),
                                Parameter.createInput("command.general.argument.contextName", "command.general.argumentDescription.contextName", true),
                                Parameter.createInput("command.general.argument.seconds", "command.general.argumentDescription.seconds", true))
                        .build(),
                CommandCategory.BOT_CONFIG);
    }


    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        Optional<Command> command = parser.getCommand(args[1]);
        String cmd = args[0];

        if (command.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.COMMAND_SEARCH_EMPTY, wrapper);
            return;
        }

        if (isSubCommand(cmd, 0)) {
            setNsfw(args, command.get(), wrapper);
            return;
        }

        if (isSubCommand(cmd, 1)) {
            setAdminOnly(args, command.get(), wrapper);
            return;
        }
        if (isSubCommand(cmd, 2) || isSubCommand(cmd, 3)) {
            setCooldown(cmd, args, command.get(), wrapper);
            return;
        }
    }

    private void setCooldown(String cmd, String[] args, Command context,
                             EventWrapper wrapper) {
        OptionalInt seconds = ArgumentParser.parseInt(args[2]);


        if (seconds.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, wrapper);
            return;
        }

        boolean userCooldown = isSubCommand(cmd, 2);

        if (userCooldown) {
            if (!commandData.setUserCooldown(context, seconds.getAsInt(), wrapper)) {
                return;
            }
        } else {
            if (!commandData.setGuildCooldown(context, seconds.getAsInt(), wrapper)) {
                return;
            }
        }
        MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace(
                "**" + (userCooldown ? M_SET_USER_COOLDOWN.tag : M_SET_GUILD_COOLDOWN.tag) + "**",
                wrapper, "\"" + context.getCommandName().toUpperCase() + "\"",
                seconds.getAsInt() + ""), wrapper.getMessageChannel());
    }


    private void setAdminOnly(String[] args, Command command, EventWrapper wrapper) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, wrapper);
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (!commandData.setAdmin(command, state, wrapper)) {
            return;
        }

        if (state) {
            MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace("**" + M_ACTIVATED_ADMIN + "**",
                    wrapper, "\"" + command.getCommandName().toUpperCase() + "\""),
                    wrapper.getMessageChannel());

        } else {
            MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace("**" + M_DEACTIVATED_ADMIN + "**",
                    wrapper, "\"" + command.getCommandName().toUpperCase() + "\""),
                    wrapper.getMessageChannel());
        }
    }

    private void setNsfw(String[] args, Command command, EventWrapper wrapper) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, wrapper);
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (!commandData.setNsfw(command, state, wrapper)) {
            return;
        }

        if (state) {
            MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace("**" + M_ACTIVATED_NSFW + "**",
                    wrapper, "\"" + command.getCommandName().toUpperCase() + "\""),
                    wrapper.getMessageChannel());

        } else {
            MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace("**" + M_DEACTIVATED_NSFW + "**",
                    wrapper, "\"" + command.getCommandName().toUpperCase() + "\""),
                    wrapper.getMessageChannel());
        }
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }

    @Override
    public void addDataSource(DataSource source) {
        commandData = new CommandData(source);
    }

}
