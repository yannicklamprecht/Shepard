package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale.C_ADMIN;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale.C_NSFW;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale.M_ACTIVATED_ADMIN;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale.M_ACTIVATED_NSFW;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale.M_DEACTIVATED_ADMIN;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale.M_DEACTIVATED_NSFW;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale.M_SET_GUILD_COOLDOWN;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale.M_SET_USER_COOLDOWN;

public class ManageContext extends Command {

    /**
     * Creates a new manage context command object.
     */
    public ManageContext() {
        commandName = "manageContext";
        commandAliases = new String[] {"mc"};
        commandDesc = ManageContextLocale.DESCRIPTION.tag;
        commandArgs = new CommandArg[] {
                new CommandArg("context name", true,
                        new SubArg("context name", GeneralLocale.A_CONTEXT_NAME.tag)),
                new CommandArg("action", true,
                        new SubArg("setNsfw", C_NSFW.tag, true),
                        new SubArg("setAdminOnly", C_ADMIN.tag, true),
                        new SubArg("setUserCooldown", ManageContextLocale.C_USER_COOLDOWN.tag, true),
                        new SubArg("setGuildCooldown", ManageContextLocale.C_GUILD_COOLDOWN.tag, true)),
                new CommandArg("value", true,
                        new SubArg("boolean", GeneralLocale.A_BOOLEAN.tag),
                        new SubArg("boolean", GeneralLocale.A_BOOLEAN.tag),
                        new SubArg("seconds", GeneralLocale.A_SECONDS.tag),
                        new SubArg("seconds", GeneralLocale.A_SECONDS.tag)
                )
        };
        category = ContextCategory.BOT_CONFIG;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String contextName = ArgumentParser.getContextName(args[0], messageContext);
        String cmd = args[1];
        CommandArg arg = commandArgs[1];

        if (contextName == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND, messageContext.getTextChannel());
            return;
        }

        if (arg.isSubCommand(cmd, 0)) {
            setNsfw(args, contextName, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 1)) {
            setAdminOnly(args, contextName, messageContext);
            return;
        }
        if (arg.isSubCommand(cmd, 2) || arg.isSubCommand(cmd, 3)) {
            setCooldown(cmd, args, contextName, messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
    }

    private void setCooldown(String cmd, String[] args, String contextName, MessageEventDataWrapper messageContext) {
        Integer seconds = ArgumentParser.parseInt(args[2]);


        if (seconds == null) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext.getTextChannel());
            return;
        }

        boolean userCooldown = commandArgs[1].isSubCommand(cmd, 2);

        if (userCooldown) {
            if (!ContextData.setContextUserCooldown(contextName, seconds, messageContext)) {
                return;
            }
        } else {
            if (!ContextData.setContextGuildCooldown(contextName, seconds, messageContext)) {
                return;
            }
        }
        MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace(
                "**" + (userCooldown ? M_SET_USER_COOLDOWN.tag : M_SET_GUILD_COOLDOWN.tag) + "**",
                messageContext.getGuild(), "\"" + contextName.toUpperCase() + "\"",
                seconds + ""), messageContext.getTextChannel());
    }


    private void setAdminOnly(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getTextChannel());
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (!ContextData.setContextAdmin(contextName, state, messageContext)) {
            return;
        }

        if (state) {
            MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace("**" + M_ACTIVATED_ADMIN + "**",
                    messageContext.getGuild(), "\"" + contextName.toUpperCase() + "\""),
                    messageContext.getTextChannel());

        } else {
            MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace("**" + M_DEACTIVATED_ADMIN + "**",
                    messageContext.getGuild(), "\"" + contextName.toUpperCase() + "\""),
                    messageContext.getTextChannel());
        }
    }

    private void setNsfw(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getTextChannel());
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (!ContextData.setContextNsfw(contextName, state, messageContext)) {
            return;
        }

        if (state) {
            MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace("**" + M_ACTIVATED_NSFW + "**",
                    messageContext.getGuild(), "\"" + contextName.toUpperCase() + "\""),
                    messageContext.getTextChannel());

        } else {
            MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace("**" + M_DEACTIVATED_NSFW + "**",
                    messageContext.getGuild(), "\"" + contextName.toUpperCase() + "\""),
                    messageContext.getTextChannel());
        }
    }
}
