package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.collections.LatestCommandsCollection;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import static de.eldoria.shepard.localization.enums.commands.admin.RepeatCommandLocale.DESCRIPTION;

public class RepeatCommand extends Command {
    public RepeatCommand() {
        commandName = "repeatCommand";
        commandDesc = DESCRIPTION.tag;
        commandAliases = new String[] {"repeat", "rc"};
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        LatestCommandsCollection.SavedCommand latestCommand = LatestCommandsCollection.getInstance()
                .getLatestCommand(messageContext.getGuild(), messageContext.getAuthor());

        if (latestCommand == null) {
            MessageSender.sendSimpleError(ErrorType.NO_LAST_COMMAND_FOUND, messageContext);
            return;
        }

        if (latestCommand.getCommand().getCommandName().equals(this.getCommandName())) {
            return;
        }
        latestCommand.getCommand().executeAsync(latestCommand.getLabel(), latestCommand.getArgs(), messageContext);
    }
}
