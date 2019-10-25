package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
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

import static de.eldoria.shepard.util.Verifier.isArgument;

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
        category = ContextCategory.BOTCONFIG;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[1];
        String contextName = ArgumentParser.getContextName(args[0], messageContext);

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

        if (isArgument(cmd, "addGuild", "ag")) {
            addGuild(args, contextName, messageContext);
            return;
        }

        if (isArgument(cmd, "removeGuild", "rg")) {
            removeGuild(args, contextName, messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getChannel());
        sendCommandArgHelp("action", messageContext.getChannel());

    }

    private void addGuild(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        modifyGuild(args, contextName, ModifyType.ADD, messageContext);
    }

    private void removeGuild(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        modifyGuild(args, contextName, ModifyType.REMOVE, messageContext);
    }

    private void modifyGuild(String[] args, String contextName,
                             ModifyType modifyType, MessageEventDataWrapper receivedEvent) {
        List<String> mentions = new ArrayList<>();

        ArgumentParser.getGuilds(ArgumentParser.getRangeAsList(args, 2)).forEach(guild -> {
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
        });

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


    private void setListType(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        ListType type = ListType.getType(args[2]);

        if (type == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_LIST_TYPE,
                    messageContext.getChannel());
            return;
        }

        if (ContextData.setContextGuildListType(contextName, type, messageContext)) {
            MessageSender.sendMessage("**Changed guild list type of context \""
                            + contextName.toUpperCase() + "\" to " + type.toString() + "**",
                    messageContext.getChannel());
        }

    }

    private void setActive(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getChannel());
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (ContextData.setContextGuildCheckActive(contextName, state, messageContext)) {
            if (state) {
                MessageSender.sendMessage("**Activated guild check for context \""
                        + contextName.toUpperCase() + "\"**", messageContext.getChannel());
            } else {
                MessageSender.sendMessage("**Deactivated guild check for context \""
                        + contextName.toUpperCase() + "\"**", messageContext.getChannel());
            }
        }
    }
}
