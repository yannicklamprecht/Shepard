package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.util.Verifier;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static de.eldoria.shepard.contexts.ContextHelper.getContextName;
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
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String contextName = getContextName(args[0], receivedEvent);
        String cmd = args[1];

        if (contextName == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND,
                    receivedEvent.getChannel());
            return;
        }

        if (cmd.equalsIgnoreCase("setNSFW") || cmd.equalsIgnoreCase("nsfw")) {
            setNsfw(args, contextName, receivedEvent);
            return;
        }

        if (cmd.equalsIgnoreCase("setadminonly") || cmd.equalsIgnoreCase("admin")) {
            setAdminOnly(args, contextName, receivedEvent);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, receivedEvent.getChannel());
    }

    private void setAdminOnly(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        BooleanState bState = Verifier.checkAndGetBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN,
                    receivedEvent.getChannel());
            return;
        }

        boolean state = bState == BooleanState.TRUE;

        ContextData.setContextAdmin(contextName, state, receivedEvent);

        if (state) {
            MessageSender.sendMessage("**Activated admin and permission check for context \""
                            + contextName.toUpperCase() + "\"**",
                    receivedEvent.getChannel());

        } else {
            MessageSender.sendMessage("**Deactivated admin and permission check for context \""
                            + contextName.toUpperCase() + "\"**",
                    receivedEvent.getChannel());
        }
    }

    private void setNsfw(String[] args, String contextName, MessageReceivedEvent receivedEvent) {
        BooleanState bState = Verifier.checkAndGetBoolean(args[2]);

        if (bState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN,
                    receivedEvent.getChannel());
            return;
        }

        boolean state = bState == BooleanState.TRUE;

        ContextData.setContextNsfw(contextName, state, receivedEvent);

        if (state) {
            MessageSender.sendMessage("**Activated NSFW check for context \"" + contextName.toUpperCase() + "\"**",
                    receivedEvent.getChannel());

        } else {
            MessageSender.sendMessage("**Deactivated NSFW check for context \"" + contextName.toUpperCase() + "\"**",
                    receivedEvent.getChannel());
        }
    }
}
