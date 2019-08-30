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

public class ManageContext extends Command {

    /**
     * Creates a new manage context command object.
     */
    public ManageContext() {
        commandName = "manageContext";
        commandAliases = new String[] {"mc"};
        commandDesc = "Manage the settings of a context";
        arguments = new CommandArg[] {
                new CommandArg("context name", "Name of the context to change", true),
                new CommandArg("action", "setNSFW | setAdminOnly", true),
                new CommandArg("value", "True or False", true)};
    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String contextName = getContextName(args[0], receivedEvent);

        if (contextName == null) {
            MessageSender.sendSimpleError(ErrorType.CONTEXT_NOT_FOUND,
                    receivedEvent.getChannel());
            return;
        }

        if (args[1].equalsIgnoreCase("setNSFW")) {
            setNsfw(args, contextName, receivedEvent);
        }

        if (args[1].equalsIgnoreCase("setadminonly")) {
            setAdminOnly(args, contextName, receivedEvent);
        }
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
