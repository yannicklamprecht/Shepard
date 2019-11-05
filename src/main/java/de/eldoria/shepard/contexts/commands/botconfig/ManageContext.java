package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.BooleanState;

import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale.C_ADMIN;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale.C_NSFW;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale.M_ACTIVATED_ADMIN;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale.M_ACTIVATED_NSFW;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale.M_DEACTIVATED_ADMIN;
import static de.eldoria.shepard.localization.enums.commands.botconfig.ManageContextLocale.M_DEACTIVATED_NSFW;

public class ManageContext extends Command {

    /**
     * Creates a new manage context command object.
     */
    public ManageContext() {
        commandName = "manageContext";
        commandAliases = new String[] {"mc"};
        commandDesc = "Manage the settings of a context";
        commandArgs = new CommandArg[] {
                new CommandArg("context name", true,
                        new SubArg("context name", GeneralLocale.A_CONTEXT_NAME.tag)),
                new CommandArg("action", true,
                        new SubArg("setNsfw", C_NSFW.tag, true),
                        new SubArg("setAdminOnly", C_ADMIN.tag, true)),
                new CommandArg("boolean", true, new SubArg("boolean",
                        GeneralLocale.A_BOOLEAN.tag))
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

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
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
            MessageSender.sendMessage("**" + M_ACTIVATED_ADMIN
                    + " \"" + contextName.toUpperCase() + "\"**", messageContext.getTextChannel());

        } else {
            MessageSender.sendMessage("**" + M_DEACTIVATED_ADMIN
                    + " \"" + contextName.toUpperCase() + "\"**", messageContext.getTextChannel());
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
            MessageSender.sendMessage("**" + M_ACTIVATED_NSFW + " \"" + contextName.toUpperCase() + "\"**",
                    messageContext.getTextChannel());

        } else {
            MessageSender.sendMessage("**" + M_DEACTIVATED_NSFW + " \"" + contextName.toUpperCase() + "\"**",
                    messageContext.getTextChannel());
        }
    }
}
