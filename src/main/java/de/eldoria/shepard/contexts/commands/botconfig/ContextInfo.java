package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.contexts.ContextHelper;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.database.types.ContextSettings;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;

import static java.lang.System.lineSeparator;

public class ContextInfo extends Command {

    /**
     * Creates new context info command object.
     */
    public ContextInfo() {
        commandName = "contextInfo";
        commandDesc = "Information about context settings";
        commandAliases = new String[] {"cinfo"};
        commandArgs = new CommandArg[] {new CommandArg("context name", "name of the context", true)};
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String contextName = ContextHelper.getContextName(args[0], receivedEvent);
        if (contextName != null) {
            ContextSettings data = ContextData.getContextData(contextName, receivedEvent);

            MessageSender.sendMessage("Information about context " + contextName.toUpperCase() + lineSeparator()
                    + "```yaml" + lineSeparator()
                    + data.toString() + lineSeparator() + "```", receivedEvent.getChannel());
        } else {
            MessageSender.sendSimpleError(ErrorType.INVALID_CONTEXT, receivedEvent.getChannel());
        }
    }
}
