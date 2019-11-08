package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.contexts.commands.botconfig.enums.ModifyType;
import de.eldoria.shepard.database.ListType;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.util.BooleanState;

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

public class ManageContextUsers extends Command {
    /**
     * Creates a new Manage context user command object.
     */
    public ManageContextUsers() {
        commandName = "manageContextUser";
        commandAliases = new String[] {"mcu"};
        commandDesc = DESCRIPTION.tag;
        commandArgs = new CommandArg[] {
                new CommandArg("context name", true,
                        new SubArg("context name", A_CONTEXT_NAME.tag)),
                new CommandArg("action", true,
                        new SubArg("setActive", C_SET_ACTIVE.tag, true),
                        new SubArg("setListType", C_SET_LIST_TYPE.tag, true),
                        new SubArg("addUser", C_ADD_USER.tag, true),
                        new SubArg("removeUser", C_REMOVE_USER.tag, true)),
                new CommandArg("value", true,
                        new SubArg("setActive", A_BOOLEAN.tag),
                        new SubArg("setListType", A_LIST_TYPE.tag),
                        new SubArg("addUser", A_USERS.tag),
                        new SubArg("removeUser", A_USERS.tag))
        };
        category = ContextCategory.BOT_CONFIG;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String contextName = ArgumentParser.getContextName(args[0], messageContext);
        String cmd = args[1];
        CommandArg arg = commandArgs[1];

        if (contextName == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND, messageContext.getTextChannel());
            return;
        }

        if (arg.isSubCommand(cmd, 0)) {
            setActive(args, contextName, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 1)) {
            setListType(args, contextName, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 2)) {
            addUser(args, contextName, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 3)) {
            removeUser(args, contextName, messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());

    }

    private void manageUser(String[] args, String contextName,
                            ModifyType modifyType, MessageEventDataWrapper messageContext) {
        List<String> mentions = new ArrayList<>();

        ArgumentParser.getGuildUsers(messageContext.getGuild(),
                ArgumentParser.getRangeAsList(args, 2))
                .forEach(user -> {
                    if (modifyType == ModifyType.ADD) {
                        if (!ContextData.addContextUser(contextName, user, messageContext)) {
                            return;
                        }
                    } else if (!ContextData.removeContextUser(contextName, user, messageContext)) {
                        return;
                    }
                    mentions.add(user.getAsMention());
                });

        String names = String.join(System.lineSeparator(), mentions);

        if (modifyType == ModifyType.ADD) {
            MessageSender.sendSimpleTextBox(localizeAllAndReplace(M_ADDED_USERS.tag, messageContext.getGuild(),
                    " **" + contextName.toUpperCase() + "**"), names, messageContext.getTextChannel());

        } else {
            MessageSender.sendSimpleTextBox(localizeAllAndReplace(M_REMOVED_USERS.tag, messageContext.getGuild(),
                    " **" + contextName.toUpperCase() + "**"), names, messageContext.getTextChannel());
        }
    }

    private void addUser(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        manageUser(args, contextName, ModifyType.ADD, messageContext);
    }

    private void removeUser(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        manageUser(args, contextName, ModifyType.REMOVE, messageContext);
    }

    private void setListType(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        ListType type = ListType.getType(args[2]);

        if (type == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_LIST_TYPE, messageContext.getTextChannel());
            return;
        }

        if (ContextData.setContextUserListType(contextName, type, messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_CHANGED_LIST_TYPE.tag,
                    messageContext.getGuild(), "**" + contextName.toUpperCase() + "**"
                            + "**" + type.toString() + "**"), messageContext.getTextChannel());
        }
    }

    private void setActive(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getTextChannel());
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (!ContextData.setContextUserCheckActive(contextName, state, messageContext)) {
            return;
        }

        if (state) {
            MessageSender.sendMessage(localizeAllAndReplace(M_ACTIVATED_CHECK.tag, messageContext.getGuild(),
                    "**" + contextName.toUpperCase() + "**"), messageContext.getTextChannel());
        } else {
            MessageSender.sendMessage(localizeAllAndReplace(M_DEACTIVATED_CHECK.tag, messageContext.getGuild(),
                    "**" + contextName.toUpperCase() + "**"), messageContext.getTextChannel());
        }
    }
}
