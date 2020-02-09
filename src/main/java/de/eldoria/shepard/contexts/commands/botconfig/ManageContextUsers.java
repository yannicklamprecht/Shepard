package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.ContextSensitive;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArgument;
import de.eldoria.shepard.contexts.commands.argument.SubArgument;
import de.eldoria.shepard.contexts.commands.botconfig.enums.ModifyType;
import de.eldoria.shepard.database.ListType;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.util.ArrayList;
import java.util.List;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_BOOLEAN;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_CONTEXT_NAME;
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
        commandName = "manageContextUser";
        commandAliases = new String[] {"mcu"};
        commandDesc = DESCRIPTION.tag;
        commandArguments = new CommandArgument[] {
                new CommandArgument("context name", true,
                        new SubArgument("context name", A_CONTEXT_NAME.tag)),
                new CommandArgument("action", true,
                        new SubArgument("setActive", C_SET_ACTIVE.tag, true),
                        new SubArgument("setListType", C_SET_LIST_TYPE.tag, true),
                        new SubArgument("addUser", C_ADD_USER.tag, true),
                        new SubArgument("removeUser", C_REMOVE_USER.tag, true)),
                new CommandArgument("value", true,
                        new SubArgument("setActive", A_BOOLEAN.tag),
                        new SubArgument("setListType", A_LIST_TYPE.tag),
                        new SubArgument("addUser", A_USERS.tag),
                        new SubArgument("removeUser", A_USERS.tag))
        };
        category = ContextCategory.BOT_CONFIG;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        ContextSensitive context = ArgumentParser.getContext(args[0], messageContext);
        String cmd = args[1];
        CommandArgument arg = commandArguments[1];

        if (context == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND, messageContext.getTextChannel());
            return;
        }

        if (arg.isSubCommand(cmd, 0)) {
            setActive(args, context, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 1)) {
            setListType(args, context, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 2)) {
            addUser(args, context, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 3)) {
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
