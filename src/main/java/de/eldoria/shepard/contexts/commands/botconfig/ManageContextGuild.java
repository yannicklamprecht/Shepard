package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.contexts.commands.botconfig.enums.ModifyType;
import de.eldoria.shepard.database.ListType;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.localization.enums.GeneralLocale;
import de.eldoria.shepard.localization.enums.botconfig.ManageContextGuildLocale;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.util.BooleanState;

import java.util.ArrayList;
import java.util.List;

import static de.eldoria.shepard.localization.enums.GeneralLocale.*;
import static de.eldoria.shepard.localization.enums.botconfig.ManageContextGuildLocale.*;
import static de.eldoria.shepard.util.Verifier.isArgument;

public class ManageContextGuild extends Command {
    /**
     * Creates a new Manage context guild command object.
     */
    public ManageContextGuild() {
        commandName = "manageContextGuild";
        commandAliases = new String[] {"mcg"};
        commandDesc = DESCRIPTION.replacement;
        commandArgs = new CommandArg[] {
                new CommandArg("context name", true,
                        new SubArg("context name", A_CONTEXT_NAME.replacement)),
                new CommandArg("action", true,
                        new SubArg("setActive", C_SET_ACTIVE.replacement, true),
                        new SubArg("setListType", C_SET_LIST_TYPE.replacement, true),
                        new SubArg("addGuild", C_ADD_GUILD.replacement, true),
                        new SubArg("removeGuild", C_REMOVE_GUILD.replacement, true)),
                new CommandArg("value", true,
                        new SubArg("setActive", A_BOOLEAN.replacement),
                        new SubArg("setListType", A_LIST_TYPE.replacement),
                        new SubArg("addGuild", A_GUILDS.replacement),
                        new SubArg("removeGuild", A_GUILDS.replacement))
        };
        category = ContextCategory.BOTCONFIG;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[1];
        String contextName = ArgumentParser.getContextName(args[0], messageContext);

        if (contextName == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND,
                    messageContext);
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

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext);
        sendCommandArgHelp("action", messageContext.getChannel());

    }

    private void addGuild(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        modifyGuild(args, contextName, ModifyType.ADD, messageContext);
    }

    private void removeGuild(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        modifyGuild(args, contextName, ModifyType.REMOVE, messageContext);
    }

    private void modifyGuild(String[] args, String contextName,
                             ModifyType modifyType, MessageEventDataWrapper messageContext) {
        List<String> mentions = new ArrayList<>();

        ArgumentParser.getGuilds(ArgumentParser.getRangeAsList(args, 2)).forEach(guild -> {
            if (modifyType == ModifyType.ADD) {
                if (!ContextData.addContextGuild(contextName, guild, messageContext)) {
                    return;
                }
            } else {
                if (!ContextData.removeContextGuild(contextName, guild, messageContext)) {
                    return;
                }
            }
            mentions.add(guild.getName());
        });

        String names = String.join(System.lineSeparator(), mentions);

        if (modifyType == ModifyType.ADD) {
            MessageSender.sendSimpleTextBox(M_ADDED_GUILDS + " **"
                    + contextName.toUpperCase() + "**", names, messageContext);

        } else {
            MessageSender.sendSimpleTextBox(M_REMOVED_GUILDS + " **"
                    + contextName.toUpperCase() + "**", names, messageContext);
        }
    }


    private void setListType(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        ListType type = ListType.getType(args[2]);

        if (type == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_LIST_TYPE, messageContext);
            return;
        }

        if (ContextData.setContextGuildListType(contextName, type, messageContext)) {
            MessageSender.sendMessage(locale.getReplacedString(M_CHANGED_LIST_TYPE.localeCode,
                    messageContext.getGuild(), "**" + contextName.toUpperCase() + "**")
                    + "**" + type.toString() + "**", messageContext);
        }

    }

    private void setActive(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext);
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (ContextData.setContextGuildCheckActive(contextName, state, messageContext)) {
            if (state) {
                MessageSender.sendMessage(M_ACTIVATED_CHECK.replacement + "**"
                        + contextName.toUpperCase() + "**", messageContext);
            } else {
                MessageSender.sendMessage(M_DEACTIVATED_CHECK + "**"
                        + contextName.toUpperCase() + "**", messageContext);
            }
        }
    }
}
