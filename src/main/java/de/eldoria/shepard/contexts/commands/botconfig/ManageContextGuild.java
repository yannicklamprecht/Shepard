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
        commandArgs = new CommandArg[] {
                new CommandArg("context name", "Name of the context to change", true),
                new CommandArg("action",
                        "**set__A__ctive** -> Enables/Disables Guild Check for Command" + System.lineSeparator()
                                + "**set__L__ist__T__ype** -> Defines it the list should be used as White or Blacklist"
                                + System.lineSeparator()
                                + "**__a__dd__G__uild** -> Adds a guild to the list" + System.lineSeparator()
                                + "**__r__emove__G__uild** -> Removes a guild from the list", true),
                new CommandArg("value",
                        "**setActive** -> 'true' or 'false'" + System.lineSeparator()
                                + "**setListType** -> 'BLACKLIST' or 'WHITELIST'. "
                                + "Defines as which Type the guild list should be used" + System.lineSeparator()
                                + "**addGuild** -> Add a guild to the list (Multiple guilds possible)"
                                + System.lineSeparator()
                                + "**removeguild** -> Removes a guild from the list (Multiple guilds possible", true)};
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String cmd = args[1];
        String contextName = getContextName(args[0], receivedEvent);

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

        if (cmd.equalsIgnoreCase("addGuild") || cmd.equalsIgnoreCase("ag")) {
            addGuild(args, contextName, receivedEvent);
            return;
        }

        if (cmd.equalsIgnoreCase("removeGuild") || cmd.equalsIgnoreCase("rg")) {
            removeGuild(args, contextName, receivedEvent);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, receivedEvent.getChannel());
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
                        if (!ContextData.addContextGuild(contextName, guild, receivedEvent)) {
                            return;
                        }

                    } else {
                        if (!ContextData.removeContextGuild(contextName, guild, receivedEvent)) {
                            return;
                        }

                    }
                    mentions.add(guild.getName());
                }
            }
        }

        String names = String.join(System.lineSeparator(), mentions);

        if (modifyType == ModifyType.ADD) {
            MessageSender.sendSimpleTextBox("Added following guilds to context \""
                            + contextName.toUpperCase() + "\"", names + "**",
                    receivedEvent.getChannel());

        } else {
            MessageSender.sendSimpleTextBox("Removed following guilds from context \""
                            + contextName.toUpperCase() + "\"", names + "**",
                    receivedEvent.getChannel());
        }

    }


    private void setListType(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        ListType type = ListType.getType(args[2]);

        if (type == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_LIST_TYPE,
                    receivedEvent.getChannel());
            return;
        }

        if (ContextData.setContextGuildListType(contextName, type, receivedEvent)) {
            MessageSender.sendMessage("**Changed guild list type of context \""
                            + contextName.toUpperCase() + "\" to " + type.toString() + "**",
                    receivedEvent.getChannel());
        }

    }

    private void setActive(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        BooleanState bState = Verifier.checkAndGetBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN,
                    receivedEvent.getChannel());
            return;
        }

        boolean state = bState == BooleanState.TRUE;

        if (ContextData.setContextGuildCheckActive(contextName, state, receivedEvent)) {
            if (state) {
                MessageSender.sendMessage("**Activated guild check for context \""
                        + contextName.toUpperCase() + "\"**", receivedEvent.getChannel());
            } else {
                MessageSender.sendMessage("**Deactivated guild check for context \""
                        + contextName.toUpperCase() + "\"**", receivedEvent.getChannel());
            }
        }
    }
}
