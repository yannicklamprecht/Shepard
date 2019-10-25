package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.BooleanState;

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
                new CommandArg("context name", "Name of the context to change", true),
                new CommandArg("action",
                        "**set__NSFW__** -> Sets the context as nsfw" + lineSeparator()
                                + "**set__Admin__Only** -> Marks a command as admin only. "
                                + "Command can only used from users"
                                + " which are admin on a guild or when they have the permission on the guild", true),
                new CommandArg("value", "True or False", true)};
        category = ContextCategory.BOTCONFIG;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String contextName = ArgumentParser.getContextName(args[0], messageContext);
        String cmd = args[1];

        if (contextName == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND,
                    messageContext.getChannel());
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

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getChannel());
    }

    private void setAdminOnly(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getChannel());
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (!ContextData.setContextAdmin(contextName, state, messageContext)) {
            return;
        }

        if (state) {
            MessageSender.sendMessage("**Activated admin and permission check for context \""
                            + contextName.toUpperCase() + "\"**",
                    messageContext.getChannel());

        } else {
            MessageSender.sendMessage("**Deactivated admin and permission check for context \""
                            + contextName.toUpperCase() + "\"**",
                    messageContext.getChannel());
        }
    }

    private void setNsfw(String[] args, String contextName, MessageEventDataWrapper messageContext) {
        BooleanState bState = ArgumentParser.getBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getChannel());
            return;
        }

        boolean state = bState.stateAsBoolean;

        if (!ContextData.setContextNsfw(contextName, state, messageContext)) {
            return;
        }

        if (state) {
            MessageSender.sendMessage("**Activated NSFW check for context \"" + contextName.toUpperCase() + "\"**",
                    messageContext.getChannel());

        } else {
            MessageSender.sendMessage("**Deactivated NSFW check for context \"" + contextName.toUpperCase() + "\"**",
                    messageContext.getChannel());
        }
    }
}
