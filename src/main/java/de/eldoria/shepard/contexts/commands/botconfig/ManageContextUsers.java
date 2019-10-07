package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.botconfig.enums.ModifyType;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.ListType;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.util.Verifier;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.eldoria.shepard.contexts.ContextHelper.getContextName;
import static de.eldoria.shepard.util.Verifier.isArgument;

public class ManageContextUsers extends Command {
    /**
     * Creates a new Manage context user command object.
     */
    public ManageContextUsers() {
        commandName = "manageContextUser";
        commandAliases = new String[] {"mcu"};
        commandDesc = "Manage which user can use a context.";
        commandArgs = new CommandArg[] {
                new CommandArg("context name", "Name of the context to change", true),
                new CommandArg("action",
                        "**set__A__ctive** -> Enables/Disables User Check for Command" + System.lineSeparator()
                                + "**set__L__ist__T__ype** -> Defines it the list should be used as White or Blacklist"
                                + System.lineSeparator()
                                + "**__a__dd__U__ser** -> Adds a user to the list" + System.lineSeparator()
                                + "**__r__emove__U__ser** -> Removes a user from the list", true),
                new CommandArg("value",
                        "**setActive** -> 'true' or 'false'" + System.lineSeparator()
                                + "**setListType** -> 'BLACKLIST' or 'WHITELIST'. "
                                + "Defines as which Type the user list should be used" + System.lineSeparator()
                                + "**addUser** -> Add a user to the list (Multiple users possible)"
                                + System.lineSeparator()
                                + "**removeUser** -> Removes a user from the list (Multiple users possible", true)};
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String contextName = getContextName(args[0], messageContext);
        String cmd = args[1];

        if (contextName == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND,
                    messageContext.getChannel());
            return;
        }

        if (isArgument(cmd, "setActive", "a")) {
            setActive(args, contextName, messageContext);
            return;
        }

        if (isArgument(cmd, "setListType", "lt")) {
            setListType(args, contextName, messageContext);
            return;
        }

        if (isArgument(cmd, "addUser", "au")) {
            addUser(args, contextName, messageContext);
            return;
        }

        if (isArgument("removeUser", "ru")) {
            removeUser(args, contextName, messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getChannel());
        sendCommandArgHelp("action", messageContext.getChannel());

    }

    private void manageUser(String[] args, String contextName,
                            ModifyType modifyType, MessageEventDataWrapper messageContext) {
        List<String> mentions = new ArrayList<>();

        for (String userId : Arrays.copyOfRange(args, 2, args.length)) {
            if (Verifier.isValidId(userId)) {
                User user = ShepardBot.getJDA().getUserById(DbUtil.getIdRaw(userId));
                if (user != null) {
                    if (modifyType == ModifyType.ADD) {
                        if (!ContextData.addContextUser(contextName, user, messageContext)) {
                            return;
                        }
                    } else {
                        if (!ContextData.removeContextUser(contextName, user, messageContext)) {
                            return;
                        }
                    }
                    mentions.add(user.getAsMention());
                }
            }
        }

        String names = String.join(System.lineSeparator(), mentions);

        if (modifyType == ModifyType.ADD) {
            MessageSender.sendSimpleTextBox("Added following users to context \""
                            + contextName.toUpperCase() + "\"", names + "**",
                    messageContext.getChannel());
        } else {
            MessageSender.sendSimpleTextBox("Removed following users from context \""
                            + contextName.toUpperCase() + "\"", names + "**",
                    messageContext.getChannel());
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
            MessageSender.sendSimpleError(ErrorType.INVALID_LIST_TYPE,
                    messageContext.getChannel());
            return;
        }

        if (ContextData.setContextUserListType(contextName, type, messageContext)) {
            MessageSender.sendMessage("**Changed user list type of context \""
                            + contextName.toUpperCase() + "\" to " + type.toString() + "**",
                    messageContext.getChannel());
        }

    }

    private void setActive(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        BooleanState bState = Verifier.checkAndGetBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN,
                    messageContext.getChannel());
            return;
        }

        boolean state = bState == BooleanState.TRUE;

        if (!ContextData.setContextUserCheckActive(contextName, state, messageContext)) {
            return;
        }

        if (state) {
            MessageSender.sendMessage("**Activated user check for context \"" + contextName.toUpperCase() + "\"**",
                    messageContext.getChannel());

        } else {
            MessageSender.sendMessage("**Deactivated user check for context \"" + contextName.toUpperCase() + "\"**",
                    messageContext.getChannel());

        }
    }
}
