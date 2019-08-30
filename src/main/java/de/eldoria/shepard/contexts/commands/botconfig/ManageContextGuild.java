package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.botconfig.enums.ModifyType;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.ListType;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.util.Verifier;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.eldoria.shepard.contexts.ContextHelper.getContextName;

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
                new CommandArg("action",
                        "**setActive** -> Enables/Disables Guild Check for Command" + System.lineSeparator()
                                + "**setListType** -> Defines it the list should be used as White or Blacklist"
                                + System.lineSeparator()
                                + "**addUser** -> Adds a guild to the list" + System.lineSeparator()
                                + "**removeUser** -> Removes a guild from the list", true),
                new CommandArg("value",
                        "**setActive** -> 'true' or 'false'" + System.lineSeparator()
                                + "**setListType** -> 'BLACKLIST' or 'WHITELIST'. "
                                + "Defines as which Type the guild list should be used" + System.lineSeparator()
                                + "**addUser** -> Add a guild to the list (Multiple guilds possible)"
                                + System.lineSeparator()
                                + "**removeUser** -> Removes a guild from the list (Multiple guilds possible", true)};
    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String contextName = getContextName(args[0], receivedEvent);

        if (contextName == null) {
            MessageSender.sendSimpleError("Context not found. Please use the context name or an alias.",
                    receivedEvent.getChannel());
            return;
        }

        if (args[1].equalsIgnoreCase("setActive")) {
            setActive(args, contextName, receivedEvent);
            return;
        }

        if (args[1].equalsIgnoreCase("setListType")) {
            setListType(args, contextName, receivedEvent);
            return;
        }

        if (args[1].equalsIgnoreCase("addGuild")) {
            addGuild(args, contextName, receivedEvent);
            return;
        }

        if (args[1].equalsIgnoreCase("removeGuild")) {
            removeGuild(args, contextName, receivedEvent);
            return;
        }

        MessageSender.sendSimpleError("Invalid Argument for action.", receivedEvent.getChannel());
        sendCommandArgHelp("action", receivedEvent.getChannel());

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

        for (String guildId : Arrays.copyOfRange(args, 2, args.length)) {
            if (Verifier.isValidId(guildId)) {
                Guild guild = ShepardBot.getJDA().getGuildById(DbUtil.getIdRaw(guildId));
                if (guild != null) {
                    if (modifyType == ModifyType.ADD) {
                        ContextData.addContextGuild(contextName, guild, receivedEvent);
                    } else {
                        ContextData.removeContextGuild(contextName, guild, receivedEvent);
                    }
                    mentions.add(guild.getName());
                }
            }
        }

        String names = String.join(System.lineSeparator(), mentions);

        if (modifyType == ModifyType.ADD) {
            MessageSender.sendSimpleTextBox("Added following guilds to context \""
                            + contextName.toUpperCase() + "\"", names,
                    receivedEvent.getChannel());

        } else {
            MessageSender.sendSimpleTextBox("Removed following guilds from context \""
                            + contextName.toUpperCase() + "\"", names,
                    receivedEvent.getChannel());
        }

    }


    private void setListType(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        ListType type = ListType.getType(args[2]);

        if (type == null) {
            MessageSender.sendSimpleError("Invalid Input. Only 'blacklist' or 'whitelist are valid inputs",
                    receivedEvent.getChannel());
            return;
        }

        ContextData.setContextGuildListType(contextName, type, receivedEvent);

        MessageSender.sendMessage("**Changed guild list type of context \""
                        + contextName.toUpperCase() + "\" to " + type.toString(),
                receivedEvent.getChannel());
    }

    private void setActive(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        BooleanState bState = Verifier.checkAndGetBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError("Invalid input. Only 'true' and 'false' are valid inputs.",
                    receivedEvent.getChannel());
            return;
        }

        boolean state = bState == BooleanState.TRUE;

        ContextData.setContextGuildCheckActive(contextName, state, receivedEvent);

        if (state) {
            MessageSender.sendMessage("**Activated guild check for context \"" + contextName.toUpperCase() + "\"**",
                    receivedEvent.getChannel());

        } else {
            MessageSender.sendMessage("**Deactivated guild check for context \"" + contextName.toUpperCase() + "\"**",
                    receivedEvent.getChannel());
        }
    }
}
