package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.ContextSensitive;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.Parameter;
import de.eldoria.shepard.contexts.commands.argument.SubCommand;
import de.eldoria.shepard.contexts.commands.botconfig.enums.ModifyType;
import de.eldoria.shepard.database.ListType;
import de.eldoria.shepard.database.queries.commands.ContextData;
import de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextUserLocale;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.util.ArrayList;
import java.util.List;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_CONTEXT_NAME;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_GUILDS;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_USERS;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_BOOLEAN;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_CONTEXT_NAME;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_GUILDS;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_USERS;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextUserLocale.A_LIST_TYPE;
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
 * Manage the user settings of a registered and active {@link ContextSensitive}.
 */
public class ManageContextUsers extends Command {
    /**
     * Creates a new Manage context user command object.
     */
    public ManageContextUsers() {
        super("manageContextUser",
                new String[] {"mcu"},
                DESCRIPTION.tag,
                SubCommand.builder("manageContextUser")
                        .addSubcommand(C_SET_ACTIVE.tag,
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true),
                                Parameter.createCommand("setActive"),
                                Parameter.createInput(A_BOOLEAN.tag, null, true))
                        .addSubcommand(C_SET_LIST_TYPE.tag,
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true),
                                Parameter.createCommand("setListType"),
                                Parameter.createInput(A_LIST_TYPE.tag, ManageContextUserLocale.AD_LIST_TYPE.tag, true))
                        .addSubcommand(C_ADD_USER.tag,
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true),
                                Parameter.createCommand("addUser"),
                                Parameter.createInput(A_USERS.tag, AD_USERS.tag, true))
                        .addSubcommand(C_REMOVE_USER.tag,
                                Parameter.createInput(A_CONTEXT_NAME.tag, AD_CONTEXT_NAME.tag, true),
                                Parameter.createCommand("removeUser"),
                                Parameter.createInput(A_USERS.tag, AD_USERS.tag, true))
                        .build(),
                ContextCategory.BOT_CONFIG);
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        ContextSensitive context = ArgumentParser.getContext(args[0], messageContext);
        String cmd = args[1];
        SubCommand arg = subCommands[1];

        if (context == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND, messageContext.getTextChannel());
            return;
        }

        if (isSubCommand(cmd, 0)) {
            setActive(args, context, messageContext);
            return;
        }

        if (isSubCommand(cmd, 1)) {
            setListType(args, context, messageContext);
            return;
        }

        if (isSubCommand(cmd, 2)) {
            addUser(args, context, messageContext);
            return;
        }

        if (isSubCommand(cmd, 3)) {
            removeUser(args, context, messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());

    }

    private void manageUser(String[] args, ContextSensitive context,
                            ModifyType modifyType, MessageEventDataWrapper messageContext) {
        List<String> mentions = new ArrayList<>();

        ArgumentParser.getGuildUsers(messageContext.getGuild(),
                ArgumentParser.getRangeAsList(args, 2))
                .forEach(user -> {
                    if (modifyType == ModifyType.ADD) {
                        if (!ContextData.addContextUser(context, user, messageContext)) {
                            return;
                        }
                    } else if (!ContextData.removeContextUser(context, user, messageContext)) {
                        return;
                    }
                    mentions.add(user.getAsMention());
                });

        String names = String.join(System.lineSeparator(), mentions);

        if (modifyType == ModifyType.ADD) {
            MessageSender.sendSimpleTextBox(localizeAllAndReplace(M_ADDED_USERS.tag, messageContext.getGuild(),
                    " **" + context.getContextName().toUpperCase() + "**"), names, messageContext.getTextChannel());

        } else {
            MessageSender.sendSimpleTextBox(localizeAllAndReplace(M_REMOVED_USERS.tag, messageContext.getGuild(),
                    " **" + context.getContextName().toUpperCase() + "**"), names, messageContext.getTextChannel());
        }
    }

    private void addUser(String[] args, ContextSensitive context, MessageEventDataWrapper messageContext) {
        manageUser(args, context, ModifyType.ADD, messageContext);
    }

    private void removeUser(String[] args, ContextSensitive context, MessageEventDataWrapper messageContext) {
        manageUser(args, context, ModifyType.REMOVE, messageContext);
    }

    private void setListType(String[] args, ContextSensitive context, MessageEventDataWrapper messageContext) {
        ListType type = ListType.getType(args[2]);

        if (type == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_LIST_TYPE, messageContext.getTextChannel());
            return;
        }

        if (ContextData.setContextUserListType(context, type, messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_CHANGED_LIST_TYPE.tag,
                    messageContext.getGuild(), "**" + context.getContextName().toUpperCase() + "**"
                            + "**" + type.toString() + "**"), messageContext.getTextChannel());
        }
    }

    private void setActive(String[] args, ContextSensitive context, MessageEventDataWrapper messageContext) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getTextChannel());
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (!ContextData.setContextUserCheckActive(context, state, messageContext)) {
            return;
        }

        if (state) {
            MessageSender.sendMessage(localizeAllAndReplace(M_ACTIVATED_CHECK.tag, messageContext.getGuild(),
                    "**" + context.getContextName().toUpperCase() + "**"), messageContext.getTextChannel());
        } else {
            MessageSender.sendMessage(localizeAllAndReplace(M_DEACTIVATED_CHECK.tag, messageContext.getGuild(),
                    "**" + context.getContextName().toUpperCase() + "**"), messageContext.getTextChannel());
        }
    }
}
