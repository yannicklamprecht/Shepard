package de.chojo.shepard.contexts.commands.botconfig;

import de.chojo.shepard.ShepardBot;
import de.chojo.shepard.database.DbUtil;
import de.chojo.shepard.database.ListType;
import de.chojo.shepard.database.queries.Context;
import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.contexts.commands.Command;
import de.chojo.shepard.contexts.commands.CommandArg;
import de.chojo.shepard.util.BooleanState;
import de.chojo.shepard.util.Verifier;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.chojo.shepard.contexts.ContextHelper.getContextName;

public class ManageContextUsers extends Command {
    public ManageContextUsers() {
        commandName = "manageContextUser";
        commandAliases = new String[] {"mcu"};
        commandDesc = "Manage which user can use a context.";
        arguments = new CommandArg[] {
                new CommandArg("context name", "Name of the context to change", true),
                new CommandArg("action", "setActive|setListType|addUser|removeUser", true),
                new CommandArg("value", "setActive -> 'true' or 'false'" + System.lineSeparator()
                        + "setListType -> 'BLACKLIST' or 'WHITELIST'. Defines as which Type the user list should be used" + System.lineSeparator()
                        + "addUser -> Add a user to the list (Multiple user possible)" + System.lineSeparator()
                        + "removeUser -> Removes a user from the list (Multiple user possible", true)};
    }

    @Override
    public boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String contextName = getContextName(args[0], receivedEvent);

        if (contextName == null) {
            Messages.sendSimpleError("Context not found. Please use the context name or an alias.",
                    receivedEvent.getChannel());
            return true;
        }

        if (args[1].equalsIgnoreCase("setActive")) {
            setActive(args, contextName, receivedEvent);
            return true;
        }

        if (args[1].equalsIgnoreCase("setListType")) {
            setListType(args, contextName, receivedEvent);
            return true;
        }

        if (args[1].equalsIgnoreCase("addUser")) {
            addUser(args, contextName, receivedEvent);
            return true;
        }

        if (args[1].equalsIgnoreCase("removeUser")) {
            removeUser(args, contextName, receivedEvent);
            return true;
        }

        Messages.sendSimpleError("Invalid Argument for action.", receivedEvent.getChannel());
        sendCommandArgHelp("action", receivedEvent.getChannel());

        return true;
    }

    private void manageUser(String[] args, String contextName, ModifyType modifyType, MessageReceivedEvent receivedEvent) {
        List<String> mentions = new ArrayList<>();

        for (String s : Arrays.copyOfRange(args, 2, args.length)) {
            if (Verifier.isValidId(s)) {
                User user = ShepardBot.getJDA().getUserById(DbUtil.getIdRaw(s));
                if (user != null) {
                    if (modifyType == ModifyType.ADD) {
                        Context.addContextUser(contextName, s, receivedEvent);
                    } else {
                        Context.removeContextUser(contextName, s, receivedEvent);
                    }
                    mentions.add(user.getAsMention());
                }
            }
        }

        String names = String.join(System.lineSeparator(), mentions);

        if (modifyType == ModifyType.ADD) {
            Messages.sendSimpleTextBox("Added following users to context \"" + contextName.toUpperCase() + "\"", names,
                    receivedEvent.getChannel());
        } else {
            Messages.sendSimpleTextBox("Removed following users from context \"" + contextName.toUpperCase() + "\"", names,
                    receivedEvent.getChannel());
        }

    }

    private void addUser(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        manageUser(args,contextName,ModifyType.ADD, receivedEvent);
    }

    private void removeUser(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        manageUser(args,contextName,ModifyType.REMOVE, receivedEvent);
    }


    private void setListType(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        ListType type = ListType.getType(args[2]);

        if (type == null) {
            Messages.sendSimpleError("Invalid Input. Only 'blacklist' or 'whitelist are valid inputs",
                    receivedEvent.getChannel());
            return;
        }

        Context.setContextUserListType(contextName, type, receivedEvent);

        Messages.sendMessage("**Changed user list type of context \""
                        + contextName.toUpperCase() + "\" to " + type.toString(),
                receivedEvent.getChannel());
    }

    private void setActive(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        BooleanState bState = Verifier.checkAndGetBoolean(args[2]);

        if(bState == BooleanState.UNDEFINED){
            Messages.sendSimpleError("Invalid input. Only 'true' and 'false' are valid inputs.",
                    receivedEvent.getChannel());
            return;
        }

        boolean state = bState == BooleanState.TRUE;

        Context.setContextUserCheckActive(contextName, state, receivedEvent);

        if (state) {
            Messages.sendMessage("**Activated user check for context \"" + contextName.toUpperCase() + "\"**",
                    receivedEvent.getChannel());

        } else {
            Messages.sendMessage("**Deactivated user check for context \"" + contextName.toUpperCase() + "\"**",
                    receivedEvent.getChannel());

        }
    }
}
