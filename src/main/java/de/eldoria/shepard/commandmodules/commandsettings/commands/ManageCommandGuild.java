package de.eldoria.shepard.commandmodules.commandsettings.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.commandsettings.data.CommandData;
import de.eldoria.shepard.commandmodules.commandsettings.types.ListType;
import de.eldoria.shepard.commandmodules.commandsettings.types.ModifyType;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextGuildLocale.*;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;


/**
 * Manage the guild settings of a registered and active {@link Command}.
 */
@CommandUsage(EventContext.GUILD)
public class ManageCommandGuild extends Command implements Executable, ReqParser, ReqDataSource {
    private ArgumentParser parser;
    private CommandData commandData;

    /**
     * Creates a new Manage context guild command object.
     */
    public ManageCommandGuild() {
        super("manageCommandGuild",
                new String[]{"mcg"},
                "command.manageContextGuild.description",
                SubCommand.builder("manageCommandGuild")
                        .addSubcommand("command.manageContextGuild.subcommand.setActive",
                                Parameter.createCommand("setActive"),
                                Parameter.createInput("command.general.argument.contextName", "command.general.argumentDescription.contextName", true),
                                Parameter.createInput("command.general.argument.boolean", null, true))
                        .addSubcommand("command.manageContextGuild.subcommand.setListType",
                                Parameter.createCommand("setListType"),
                                Parameter.createInput("command.general.argument.contextName", "command.general.argumentDescription.contextName", true),
                                Parameter.createInput("command.general.argument.listType", "command.general.argumentDescription.listType", true))
                        .addSubcommand("command.manageContextGuild.subcommand.addGuild",
                                Parameter.createCommand("addGuild"),
                                Parameter.createInput("command.general.argument.contextName", "command.general.argumentDescription.contextName", true),
                                Parameter.createInput("command.general.argument.guilds", "command.general.argumentDescription.guilds", true))
                        .addSubcommand("command.manageContextGuild.subcommand.removeGuild",
                                Parameter.createCommand("removeGuild"),
                                Parameter.createInput("command.general.argument.contextName", "command.general.argumentDescription.contextName", true),
                                Parameter.createInput("command.general.argument.guilds", "command.general.argumentDescription.guilds", true))
                        .build(),
                CommandCategory.BOT_CONFIG);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String cmd = args[0];

        Optional<Command> command = parser.getCommand(args[1]);

        if (command.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.COMMAND_SEARCH_EMPTY, wrapper);
            return;
        }

        if (isSubCommand(cmd, 0)) {
            setActive(args, command.get(), wrapper);
            return;
        }

        if (isSubCommand(cmd, 1)) {
            setListType(args, command.get(), wrapper);
            return;
        }

        if (isSubCommand(cmd, 2)) {
            addGuild(args, command.get(), wrapper);
            return;
        }

        if (isSubCommand(cmd, 3)) {
            removeGuild(args, command.get(), wrapper);
            return;
        }
    }

    private void addGuild(String[] args, Command context, EventWrapper messageContext) {
        modifyGuild(args, context, ModifyType.ADD, messageContext);
    }

    private void removeGuild(String[] args, Command contextName, EventWrapper messageContext) {
        modifyGuild(args, contextName, ModifyType.REMOVE, messageContext);
    }

    private void modifyGuild(String[] args, Command context,
                             ModifyType modifyType, EventWrapper messageContext) {
        List<String> mentions = new ArrayList<>();

        parser.getGuilds(ArgumentParser.getRangeAsList(args, 2)).forEach(guild -> {
            if (modifyType == ModifyType.ADD) {
                if (!commandData.addGuild(context, guild, messageContext)) {
                    return;
                }
            } else {
                if (!commandData.removeGuild(context, guild, messageContext)) {
                    return;
                }
            }
            mentions.add(guild.getName());
        });

        String names = String.join(System.lineSeparator(), mentions);

        if (modifyType == ModifyType.ADD) {
            MessageSender.sendSimpleTextBox(localizeAllAndReplace(M_ADDED_GUILDS.tag,
                    messageContext, "**" + context.getCommandName().toUpperCase() + "**"),
                    names, messageContext);

        } else {
            MessageSender.sendSimpleTextBox(localizeAllAndReplace(M_REMOVED_GUILDS.tag,
                    messageContext, "**" + context.getCommandName().toUpperCase() + "**"),
                    names, messageContext);
        }
    }


    private void setListType(String[] args, Command command, EventWrapper messageContext) {
        ListType type = ListType.getType(args[2]);

        if (type == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_LIST_TYPE, messageContext);
            return;
        }

        if (commandData.setGuildListType(command, type, messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_CHANGED_LIST_TYPE.tag,
                    messageContext, "**" + command.getCommandName().toUpperCase() + "**",
                    "**" + type.toString() + "**"), messageContext.getMessageChannel());
        }

    }

    private void setActive(String[] args, Command command, EventWrapper messageContext) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext);
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (commandData.setGuildCheckActive(command, state, messageContext)) {
            if (state) {
                MessageSender.sendMessage(localizeAllAndReplace(M_ACTIVATED_CHECK.tag, messageContext,
                        "**" + command.getCommandName().toUpperCase() + "**"), messageContext.getMessageChannel());
            } else {
                MessageSender.sendMessage(localizeAllAndReplace(M_DEACTIVATED_CHECK.tag, messageContext,
                        "**" + command.getCommandName().toUpperCase() + "**"), messageContext.getMessageChannel());
            }
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
