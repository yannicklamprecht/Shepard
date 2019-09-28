package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.contexts.ContextHelper;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.database.types.ContextSettings;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;

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
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String contextName = ContextHelper.getContextName(args[0], messageContext);
        if (contextName != null) {
            ContextSettings data = ContextData.getContextData(contextName, messageContext);

            MessageSender.sendMessage("Information about context " + contextName.toUpperCase() + lineSeparator()
                    + "```yaml" + lineSeparator()
                    + data.toString() + lineSeparator() + "```", messageContext.getChannel());
        } else {
            MessageSender.sendSimpleErrorEmbed(ErrorType.INVALID_CONTEXT, messageContext.getChannel());
        }
    }
}
