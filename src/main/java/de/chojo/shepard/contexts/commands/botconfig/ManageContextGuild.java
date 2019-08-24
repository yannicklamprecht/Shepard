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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.chojo.shepard.contexts.ContextHelper.getContextName;

public class ManageContextGuild extends Command {
    /**
     * Creates a new Manage context guild command object.
     */
    public ManageContextGuild() {
        commandName = "manageContextGuild";
        commandAliases = new String[] {"mcg"};
        commandDesc = "Manage which guilds can use a context.";
        arguments = new CommandArg[] {
                new CommandArg("context name", "Name of the context to change", true),
                new CommandArg("action", "setActive|setListType|addGuild|removeGuild", true),
                new CommandArg("value", "setActive -> 'true' or 'false'" + System.lineSeparator()
                        + "setListType -> 'BLACKLIST' or 'WHITELIST'. "
                        + "Defines as which Type the guild list should be used"
                        + System.lineSeparator()
                        + "addGuild -> Add a guild to the list (Multiple guilds possible)" + System.lineSeparator()
                        + "removeGuild -> Removes a guild from the list (Multiple guilds possible", true)};
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

        if (args[1].equalsIgnoreCase("addGuild")) {
            addGuild(args, contextName, receivedEvent);
            return true;
        }

        if (args[1].equalsIgnoreCase("removeGuild")) {
            removeGuild(args, contextName, receivedEvent);
            return true;
        }

        Messages.sendSimpleError("Invalid Argument for action.", receivedEvent.getChannel());
        sendCommandArgHelp("action", receivedEvent.getChannel());

        return true;
    }

    private void addGuild(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        modifyGuild(args, contextName, ModifyType.ADD, receivedEvent);
    }

    private void removeGuild(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        modifyGuild(args, contextName, ModifyType.REMOVE, receivedEvent);
    }

    private void modifyGuild(String[] args, String contextName,
                             ModifyType modifyType, MessageReceivedEvent receivedEvent) {
        List<String> mentions = new ArrayList<>();

        for (String s : Arrays.copyOfRange(args, 2, args.length)) {
            if (Verifier.isValidId(s)) {
                Guild guild = ShepardBot.getJDA().getGuildById(DbUtil.getIdRaw(s));
                if (guild != null) {
                    if (modifyType == ModifyType.ADD) {
                        Context.addContextGuild(contextName, s, receivedEvent);
                    } else {
                        Context.removeContextGuild(contextName, s, receivedEvent);
                    }
                    mentions.add(guild.getName());
                }
            }
        }

        String names = String.join(System.lineSeparator(), mentions);

        if (modifyType == ModifyType.ADD) {
            Messages.sendSimpleTextBox("Added following guilds to context \""
                            + contextName.toUpperCase() + "\"", names,
                    receivedEvent.getChannel());

        } else {
            Messages.sendSimpleTextBox("Removed following guilds from context \""
                            + contextName.toUpperCase() + "\"", names,
                    receivedEvent.getChannel());
        }

    }


    private void setListType(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        ListType type = ListType.getType(args[2]);

        if (type == null) {
            Messages.sendSimpleError("Invalid Input. Only 'blacklist' or 'whitelist are valid inputs",
                    receivedEvent.getChannel());
            return;
        }

        Context.setContextGuildListType(contextName, type, receivedEvent);

        Messages.sendMessage("**Changed guild list type of context \""
                        + contextName.toUpperCase() + "\" to " + type.toString(),
                receivedEvent.getChannel());
    }

    private void setActive(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        BooleanState bState = Verifier.checkAndGetBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            Messages.sendSimpleError("Invalid input. Only 'true' and 'false' are valid inputs.",
                    receivedEvent.getChannel());
            return;
        }

        boolean state = bState == BooleanState.TRUE;

        Context.setContextGuildCheckActive(contextName, state, receivedEvent);

        if (state) {
            Messages.sendMessage("**Activated guild check for context \"" + contextName.toUpperCase() + "\"**",
                    receivedEvent.getChannel());

        } else {
            Messages.sendMessage("**Deactivated guild check for context \"" + contextName.toUpperCase() + "\"**",
                    receivedEvent.getChannel());
        }
    }
}
