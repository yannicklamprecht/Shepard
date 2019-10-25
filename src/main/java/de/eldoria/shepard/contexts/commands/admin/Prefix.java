package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;

import static de.eldoria.shepard.database.queries.PrefixData.setPrefix;
import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class Prefix extends Command {

    /**
     * Creates a new prefix command object.
     */
    public Prefix() {
        commandName = "prefix";
        commandDesc = "Manage prefix settings";
        commandArgs = new CommandArg[] {new CommandArg("action",
                "**__s__et** -> changes the prefix" + lineSeparator()
                        + "**reset** -> Sets the prefix to default", true),
                new CommandArg("value",
                        "**set** -> One or two character" + lineSeparator()
                                + "**reset** -> leave empty", false)};
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        if (isArgument(cmd, "set", "s")) {
            set(args, messageContext);
            return;
        }
        if (isArgument(cmd, "reset", "r")) {
            reset(messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getChannel());
    }

    private void reset(MessageEventDataWrapper messageContext) {
        if (setPrefix(messageContext.getGuild(), ShepardBot.getConfig().getPrefix(), messageContext)) {
            MessageSender.sendMessage("Set Prefix to '" + ShepardBot.getConfig().getPrefix() + "'",
                    messageContext.getChannel());
        }
    }

    private void set(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 1) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getChannel());
            return;
        }

        if (args[1].length() > 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_PREFIX_LENGTH, messageContext.getChannel());
            return;
        }

        if (setPrefix(messageContext.getGuild(), args[1].trim(), messageContext)) {
            MessageSender.sendMessage("Changed prefix to '" + args[1].trim() + "'", messageContext.getChannel());
        }
    }
}
