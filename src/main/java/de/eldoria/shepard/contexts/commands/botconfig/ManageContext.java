package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.localization.enums.GeneralLocale;
import de.eldoria.shepard.localization.enums.botconfig.ManageContextLocale;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.BooleanState;

import static de.eldoria.shepard.localization.enums.botconfig.ManageContextLocale.*;
import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

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
                        new SubArg("context name", GeneralLocale.A_CONTEXT_NAME.replacement)),
                new CommandArg("action", true,
                        new SubArg("setNsfw", C_NSFW.replacement),
                        new SubArg("setAdminOnly", C_ADMIN.replacement)),
                new CommandArg("boolean", true, new SubArg("boolean",
                        GeneralLocale.A_BOOLEAN.replacement))
        };
        category = ContextCategory.BOTCONFIG;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String contextName = ArgumentParser.getContextName(args[0], messageContext);
        String cmd = args[1];

        if (contextName == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND,
                    messageContext);
            return;
        }

        if (isArgument(cmd, "setNSFW", "nsfw")) {
            setNsfw(args, contextName, messageContext);
            return;
        }

        if (isArgument(cmd, "setAdminOnly", "admin")) {
            setAdminOnly(args, contextName, messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext);
    }

    private void setAdminOnly(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext);
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (!ContextData.setContextAdmin(contextName, state, messageContext)) {
            return;
        }

        if (state) {
            MessageSender.sendMessage("**" + M_ACTIVATED_ADMIN
                    + "\"" + contextName.toUpperCase() + "\"**", messageContext);

        } else {
            MessageSender.sendMessage("**" + M_DEACTIVATED_ADMIN
                    + "\"" + contextName.toUpperCase() + "\"**", messageContext);
        }
    }

    private void setNsfw(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext);
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (!ContextData.setContextNsfw(contextName, state, messageContext)) {
            return;
        }

        if (state) {
            MessageSender.sendMessage("**" + M_ACTIVATED_NSFW + "\"" + contextName.toUpperCase() + "\"**",
                    messageContext);

        } else {
            MessageSender.sendMessage("**" + M_DEACTIVATED_NSFW + "\"" + contextName.toUpperCase() + "\"**",
                    messageContext);
        }
    }
}
