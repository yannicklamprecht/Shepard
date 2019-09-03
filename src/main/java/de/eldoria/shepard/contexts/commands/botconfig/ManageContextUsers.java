package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.botconfig.enums.ModifyType;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.ListType;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.util.Verifier;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.eldoria.shepard.contexts.ContextHelper.getContextName;

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
    public void internalExecute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String contextName = getContextName(args[0], receivedEvent);
        String cmd = args[1];

        if (contextName == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND,
                    receivedEvent.getChannel());
            return;
        }

        if (cmd.equalsIgnoreCase("setActive") || cmd.equalsIgnoreCase("a")) {
            setActive(args, contextName, receivedEvent);
            return;
        }

        if (cmd.equalsIgnoreCase("setListType") || cmd.equalsIgnoreCase("lt")) {
            setListType(args, contextName, receivedEvent);
            return;
        }

        if (cmd.equalsIgnoreCase("addUser") || cmd.equalsIgnoreCase("au")) {
            addUser(args, contextName, receivedEvent);
            return;
        }

        if (cmd.equalsIgnoreCase("removeUser") || cmd.equalsIgnoreCase("ru")) {
            removeUser(args, contextName, receivedEvent);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, receivedEvent.getChannel());
        sendCommandArgHelp("action", receivedEvent.getChannel());

    }

    private void manageUser(String[] args, String contextName,
                            ModifyType modifyType, MessageReceivedEvent receivedEvent) {
        List<String> mentions = new ArrayList<>();

        for (String userId : Arrays.copyOfRange(args, 2, args.length)) {
            if (Verifier.isValidId(userId)) {
                User user = ShepardBot.getJDA().getUserById(DbUtil.getIdRaw(userId));
                if (user != null) {
                    if (modifyType == ModifyType.ADD) {


                        ContextData.addContextUser(contextName, user, receivedEvent);
                    } else {
                        ContextData.removeContextUser(contextName, user, receivedEvent);
                    }
                    mentions.add(user.getAsMention());
                }
            }
        }

        String names = String.join(System.lineSeparator(), mentions);

        if (modifyType == ModifyType.ADD) {
            MessageSender.sendSimpleTextBox("Added following users to context \""
                            + contextName.toUpperCase() + "\"", names + "**",
                    receivedEvent.getChannel());
        } else {
            MessageSender.sendSimpleTextBox("Removed following users from context \""
                            + contextName.toUpperCase() + "\"", names + "**",
                    receivedEvent.getChannel());
        }
    }

    private void addUser(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        manageUser(args, contextName, ModifyType.ADD, receivedEvent);
    }

    private void removeUser(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        manageUser(args, contextName, ModifyType.REMOVE, receivedEvent);
    }


    private void setListType(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        ListType type = ListType.getType(args[2]);

        if (type == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_LIST_TYPE,
                    receivedEvent.getChannel());
            return;
        }

        ContextData.setContextUserListType(contextName, type, receivedEvent);

        MessageSender.sendMessage("**Changed user list type of context \""
                        + contextName.toUpperCase() + "\" to " + type.toString() + "**",
                receivedEvent.getChannel());
    }

    private void setActive(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        BooleanState bState = Verifier.checkAndGetBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN,
                    receivedEvent.getChannel());
            return;
        }

        boolean state = bState == BooleanState.TRUE;

        ContextData.setContextUserCheckActive(contextName, state, receivedEvent);

        if (state) {
            MessageSender.sendMessage("**Activated user check for context \"" + contextName.toUpperCase() + "\"**",
                    receivedEvent.getChannel());

        } else {
            MessageSender.sendMessage("**Deactivated user check for context \"" + contextName.toUpperCase() + "\"**",
                    receivedEvent.getChannel());

        }
    }
}
