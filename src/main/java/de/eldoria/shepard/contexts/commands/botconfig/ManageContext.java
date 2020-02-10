package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.ContextSensitive;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArgument;
import de.eldoria.shepard.contexts.commands.argument.SubArgument;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale.*;

/**
 * Manage the basic settings of a registered and active {@link ContextSensitive}.
 */
public class ManageContext extends Command {

    /**
     * Creates a new manage context command object.
     */
    public ManageContext() {
        commandName = "manageContext";
        commandAliases = new String[] {"mc"};
        commandDesc = ManageContextLocale.DESCRIPTION.tag;
        commandArguments = new CommandArgument[] {
                new CommandArgument("context name", true,
                        new SubArgument("context name", GeneralLocale.A_CONTEXT_NAME.tag)),
                new CommandArgument("action", true,
                        new SubArgument("setNsfw", C_NSFW.tag, true),
                        new SubArgument("setAdminOnly", C_ADMIN.tag, true),
                        new SubArgument("setUserCooldown", ManageContextLocale.C_USER_COOLDOWN.tag, true),
                        new SubArgument("setGuildCooldown", ManageContextLocale.C_GUILD_COOLDOWN.tag, true)),
                new CommandArgument("value", true,
                        new SubArgument("boolean", GeneralLocale.A_BOOLEAN.tag),
                        new SubArgument("boolean", GeneralLocale.A_BOOLEAN.tag),
                        new SubArgument("seconds", GeneralLocale.A_SECONDS.tag),
                        new SubArgument("seconds", GeneralLocale.A_SECONDS.tag)
                )
        };
        category = ContextCategory.BOT_CONFIG;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        ContextSensitive context = ArgumentParser.getContext(args[0], messageContext);
        String cmd = args[1];
        CommandArgument arg = commandArguments[1];

        if (context == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND, messageContext.getTextChannel());
            return;
        }

        if (arg.isSubCommand(cmd, 0)) {
            setNsfw(args, context, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 1)) {
            setAdminOnly(args, context, messageContext);
            return;
        }
        if (arg.isSubCommand(cmd, 2) || arg.isSubCommand(cmd, 3)) {
            setCooldown(cmd, args, context, messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
    }

    private void setCooldown(String cmd, String[] args, ContextSensitive context,
                             MessageEventDataWrapper messageContext) {
        Integer seconds = ArgumentParser.parseInt(args[2]);


        if (seconds == null) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext.getTextChannel());
            return;
        }

        boolean userCooldown = commandArguments[1].isSubCommand(cmd, 2);

        if (userCooldown) {
            if (!ContextData.setContextUserCooldown(context, seconds, messageContext)) {
                return;
            }
        } else {
            if (!ContextData.setContextGuildCooldown(context, seconds, messageContext)) {
                return;
            }
        }
        MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace(
                "**" + (userCooldown ? M_SET_USER_COOLDOWN.tag : M_SET_GUILD_COOLDOWN.tag) + "**",
                messageContext.getGuild(), "\"" + context.getContextName().toUpperCase() + "\"",
                seconds + ""), messageContext.getTextChannel());
    }


    private void setAdminOnly(String[] args, ContextSensitive context, MessageEventDataWrapper messageContext) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getTextChannel());
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (!ContextData.setContextAdmin(context, state, messageContext)) {
            return;
        }

        if (state) {
            MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace("**" + M_ACTIVATED_ADMIN + "**",
                    messageContext.getGuild(), "\"" + context.getContextName().toUpperCase() + "\""),
                    messageContext.getTextChannel());

        } else {
            MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace("**" + M_DEACTIVATED_ADMIN + "**",
                    messageContext.getGuild(), "\"" + context.getContextName().toUpperCase() + "\""),
                    messageContext.getTextChannel());
        }
    }

    private void setNsfw(String[] args, ContextSensitive context, MessageEventDataWrapper messageContext) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getTextChannel());
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (!ContextData.setContextNsfw(context, state, messageContext)) {
            return;
        }

        if (state) {
            MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace("**" + M_ACTIVATED_NSFW + "**",
                    messageContext.getGuild(), "\"" + context.getContextName().toUpperCase() + "\""),
                    messageContext.getTextChannel());

        } else {
            MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace("**" + M_DEACTIVATED_NSFW + "**",
                    messageContext.getGuild(), "\"" + context.getContextName().toUpperCase() + "\""),
                    messageContext.getTextChannel());
        }
    }
}
