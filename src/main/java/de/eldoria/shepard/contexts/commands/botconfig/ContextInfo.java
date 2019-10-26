package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.database.types.ContextSettings;
import de.eldoria.shepard.localization.enums.GeneralLocale;
import de.eldoria.shepard.localization.enums.botconfig.ContextInfoLocale;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;

import static de.eldoria.shepard.localization.enums.botconfig.ContextInfoLocale.DESCRIPTION;
import static java.lang.System.lineSeparator;

public class ContextInfo extends Command {

    /**
     * Creates new context info command object.
     */
    public ContextInfo() {
        commandName = "contextInfo";
        commandDesc = DESCRIPTION.replacement;
        commandAliases = new String[] {"cinfo"};
        commandArgs = new CommandArg[] {new CommandArg("context name", true,
                new SubArg("context name", GeneralLocale.A_CONTEXT_NAME.replacement))};
        category = ContextCategory.BOTCONFIG;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String contextName = ArgumentParser.getContextName(args[0], messageContext);
        if (contextName != null) {
            ContextSettings data = ContextData.getContextData(contextName, messageContext);

            MessageSender.sendMessage("Information about context " + contextName.toUpperCase() + lineSeparator()
                    + "```yaml" + lineSeparator()
                    + data.toString() + lineSeparator() + "```", messageContext);
        } else {
            MessageSender.sendSimpleError(ErrorType.INVALID_CONTEXT, messageContext);
        }
    }
}
