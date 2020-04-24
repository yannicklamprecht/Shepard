package de.eldoria.shepard.commandmodules.commandsettings.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
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
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_CONTEXT_NAME;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_LIST_TYPE;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_USERS;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_BOOLEAN;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_CONTEXT_NAME;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_LIST_TYPE;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_USERS;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextUserLocale.C_ADD_USER;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextUserLocale.C_REMOVE_USER;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextUserLocale.C_SET_ACTIVE;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextUserLocale.C_SET_LIST_TYPE;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextUserLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextUserLocale.M_ACTIVATED_CHECK;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextUserLocale.M_ADDED_USERS;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextUserLocale.M_CHANGED_LIST_TYPE;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextUserLocale.M_DEACTIVATED_CHECK;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextUserLocale.M_REMOVED_USERS;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

/**
 * Manage the user settings of a registered and active {@link Command}.
 */
public class ManageCommandUsers extends Command implements Executable, ReqParser, ReqDataSource {
    private ArgumentParser parser;
    private CommandData commandData;

    /**
     * Creates a new Manage context user command object.
     */
    public ManageCommandUsers() {
        super("manageCommandUser",
                new String[] {"mcu"},
                DESCRIPTION.tag,
                SubCommand.builder("manageCommandUser")
                        .addSubcommand(C_SET_ACTIVE.tag,
                                Parameter.createCommand("setActive"),
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true),
                                Parameter.createInput(A_BOOLEAN.tag, null, true))
                        .addSubcommand(C_SET_LIST_TYPE.tag,
                                Parameter.createCommand("setListType"),
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true),
                                Parameter.createInput(A_LIST_TYPE.tag, AD_LIST_TYPE.tag, true))
                        .addSubcommand(C_ADD_USER.tag,
                                Parameter.createCommand("addUser"),
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true),
                                Parameter.createInput(A_USERS.tag, AD_USERS.tag, true))
                        .addSubcommand(C_REMOVE_USER.tag,
                                Parameter.createCommand("removeUser"),
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true),
                                Parameter.createInput(A_USERS.tag, AD_USERS.tag, true))
                        .build(),
                CommandCategory.BOT_CONFIG);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        Optional<Command> command = parser.getCommand(args[0], messageContext);
        String cmd = args[1];

        if (command.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND, messageContext.getTextChannel());
            return;
        }

        if (isSubCommand(cmd, 0)) {
            setActive(args, command.get(), messageContext);
            return;
        }

        if (isSubCommand(cmd, 1)) {
            setListType(args, command.get(), messageContext);
            return;
        }

        if (isSubCommand(cmd, 2)) {
            addUser(args, command.get(), messageContext);
            return;
        }

        if (isSubCommand(cmd, 3)) {
            removeUser(args, command.get(), messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());

    }

    private void manageUser(String[] args, Command context,
                            ModifyType modifyType, MessageEventDataWrapper messageContext) {
        List<String> mentions = new ArrayList<>();

        parser.getGuildUsers(messageContext.getGuild(),
                ArgumentParser.getRangeAsList(args, 2))
                .forEach(user -> {
                    if (modifyType == ModifyType.ADD) {
                        if (!commandData.addCommandUser(context, user, messageContext)) {
                            return;
                        }
                    } else if (!commandData.removeContextUser(context, user, messageContext)) {
                        return;
                    }
                    mentions.add(user.getAsMention());
                });

        String names = String.join(System.lineSeparator(), mentions);

        if (modifyType == ModifyType.ADD) {
            MessageSender.sendSimpleTextBox(localizeAllAndReplace(M_ADDED_USERS.tag, messageContext.getGuild(),
                    " **" + context.getCommandName().toUpperCase() + "**"), names, messageContext.getTextChannel());

        } else {
            MessageSender.sendSimpleTextBox(localizeAllAndReplace(M_REMOVED_USERS.tag, messageContext.getGuild(),
                    " **" + context.getCommandName().toUpperCase() + "**"), names, messageContext.getTextChannel());
        }
    }

    private void addUser(String[] args, Command context, MessageEventDataWrapper messageContext) {
        manageUser(args, context, ModifyType.ADD, messageContext);
    }

    private void removeUser(String[] args, Command context, MessageEventDataWrapper messageContext) {
        manageUser(args, context, ModifyType.REMOVE, messageContext);
    }

    private void setListType(String[] args, Command context, MessageEventDataWrapper messageContext) {
        ListType type = ListType.getType(args[2]);

        if (type == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_LIST_TYPE, messageContext.getTextChannel());
            return;
        }

        if (commandData.setContextUserListType(context, type, messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_CHANGED_LIST_TYPE.tag,
                    messageContext.getGuild(), "**" + context.getCommandName().toUpperCase() + "**",
                    "**" + type.toString() + "**"), messageContext.getTextChannel());
        }
    }

    private void setActive(String[] args, Command context, MessageEventDataWrapper messageContext) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getTextChannel());
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (!commandData.setContextUserCheckActive(context, state, messageContext)) {
            return;
        }

        if (state) {
            MessageSender.sendMessage(localizeAllAndReplace(M_ACTIVATED_CHECK.tag, messageContext.getGuild(),
                    "**" + context.getCommandName().toUpperCase() + "**"), messageContext.getTextChannel());
        } else {
            MessageSender.sendMessage(localizeAllAndReplace(M_DEACTIVATED_CHECK.tag, messageContext.getGuild(),
                    "**" + context.getCommandName().toUpperCase() + "**"), messageContext.getTextChannel());
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
