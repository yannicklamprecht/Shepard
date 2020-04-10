package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.ContextSensitive;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArgument;
import de.eldoria.shepard.contexts.commands.argument.SubArgument;
import de.eldoria.shepard.contexts.commands.botconfig.enums.ModifyType;
import de.eldoria.shepard.database.ListType;
import de.eldoria.shepard.database.queries.commands.ContextData;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.util.ArrayList;
import java.util.List;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_BOOLEAN;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_CONTEXT_NAME;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_GUILDS;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextGuildLocale.A_LIST_TYPE;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextGuildLocale.C_ADD_GUILD;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextGuildLocale.C_REMOVE_GUILD;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextGuildLocale.C_SET_ACTIVE;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextGuildLocale.C_SET_LIST_TYPE;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextGuildLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextGuildLocale.M_ACTIVATED_CHECK;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextGuildLocale.M_ADDED_GUILDS;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextGuildLocale.M_CHANGED_LIST_TYPE;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextGuildLocale.M_DEACTIVATED_CHECK;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextGuildLocale.M_REMOVED_GUILDS;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

/**
 * Manage the guild settings of a registered and active {@link ContextSensitive}.
 */
public class ManageContextGuild extends Command {
    /**
     * Creates a new Manage context guild command object.
     */
    public ManageContextGuild() {
        commandName = "manageContextGuild";
        commandAliases = new String[] {"mcg"};
        commandDesc = DESCRIPTION.tag;
        commandArguments = new CommandArgument[] {
                new CommandArgument("context name", true,
                        new SubArgument("context name", A_CONTEXT_NAME.tag)),
                new CommandArgument("action", true,
                        new SubArgument("setActive", C_SET_ACTIVE.tag, true),
                        new SubArgument("setListType", C_SET_LIST_TYPE.tag, true),
                        new SubArgument("addGuild", C_ADD_GUILD.tag, true),
                        new SubArgument("removeGuild", C_REMOVE_GUILD.tag, true)),
                new CommandArgument("value", true,
                        new SubArgument("setActive", A_BOOLEAN.tag),
                        new SubArgument("setListType", A_LIST_TYPE.tag),
                        new SubArgument("addGuild", A_GUILDS.tag),
                        new SubArgument("removeGuild", A_GUILDS.tag))
        };
        category = ContextCategory.BOT_CONFIG;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[1];
        CommandArgument arg = commandArguments[1];

        ContextSensitive context = ArgumentParser.getContext(args[0], messageContext);

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
            addGuild(args, context, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 3)) {
            removeGuild(args, context, messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());

    }

    private void addGuild(String[] args, ContextSensitive context, MessageEventDataWrapper messageContext) {
        modifyGuild(args, context, ModifyType.ADD, messageContext);
    }

    private void removeGuild(String[] args, ContextSensitive contextName, MessageEventDataWrapper messageContext) {
        modifyGuild(args, contextName, ModifyType.REMOVE, messageContext);
    }

    private void modifyGuild(String[] args, ContextSensitive context,
                             ModifyType modifyType, MessageEventDataWrapper messageContext) {
        List<String> mentions = new ArrayList<>();

        ArgumentParser.getGuilds(ArgumentParser.getRangeAsList(args, 2)).forEach(guild -> {
            if (modifyType == ModifyType.ADD) {
                if (!ContextData.addContextGuild(context, guild, messageContext)) {
                    return;
                }
            } else {
                if (!ContextData.removeContextGuild(context, guild, messageContext)) {
                    return;
                }
            }
            mentions.add(guild.getName());
        });

        String names = String.join(System.lineSeparator(), mentions);

        if (modifyType == ModifyType.ADD) {
            MessageSender.sendSimpleTextBox(localizeAllAndReplace(M_ADDED_GUILDS.tag,
                    messageContext.getGuild(), "**" + context.getContextName().toUpperCase() + "**"),
                    names, messageContext.getTextChannel());

        } else {
            MessageSender.sendSimpleTextBox(localizeAllAndReplace(M_REMOVED_GUILDS.tag,
                    messageContext.getGuild(), "**" + context.getContextName().toUpperCase() + "**"),
                    names, messageContext.getTextChannel());
        }
    }


    private void setListType(String[] args, ContextSensitive contextName, MessageEventDataWrapper messageContext) {
        ListType type = ListType.getType(args[2]);

        if (type == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_LIST_TYPE, messageContext.getTextChannel());
            return;
        }

        if (ContextData.setContextGuildListType(contextName, type, messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_CHANGED_LIST_TYPE.tag,
                    messageContext.getGuild(), "**" + contextName.getContextName().toUpperCase() + "**",
                    "**" + type.toString() + "**"), messageContext.getTextChannel());
        }

    }

    private void setActive(String[] args, ContextSensitive contextName, MessageEventDataWrapper messageContext) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getTextChannel());
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (ContextData.setContextGuildCheckActive(contextName, state, messageContext)) {
            if (state) {
                MessageSender.sendMessage(localizeAllAndReplace(M_ACTIVATED_CHECK.tag, messageContext.getGuild(),
                        "**" + contextName.getContextName().toUpperCase() + "**"), messageContext.getTextChannel());
            } else {
                MessageSender.sendMessage(localizeAllAndReplace(M_DEACTIVATED_CHECK.tag, messageContext.getGuild(),
                        "**" + contextName.getContextName().toUpperCase() + "**"), messageContext.getTextChannel());
            }
        }
    }
}
