package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.contexts.ContextHelper;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.queries.Context;
import de.eldoria.shepard.database.types.ContextData;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static java.lang.System.lineSeparator;

public class ContextInfo extends Command {

    /**
     * Creates new context info command object.
     */
    public ContextInfo() {
        commandName = "contextInfo";
        commandDesc = "Information about context settings";
        commandAliases = new String[] {"cinfo"};
        arguments = new CommandArg[] {new CommandArg("context name", "name of the context", true)};
    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String contextName = ContextHelper.getContextName(args[0], receivedEvent);
        if (contextName != null) {
            ContextData data = Context.getContextData(contextName, receivedEvent);
            MessageSender.sendMessage( "Information about context " + contextName.toUpperCase() + lineSeparator()
                    + "```yaml" + lineSeparator()
                    + data.toString() + lineSeparator() + "```", receivedEvent.getChannel());
        } else {
            MessageSender.sendSimpleError("Unkown Context", receivedEvent.getChannel());
        }
    }
}
