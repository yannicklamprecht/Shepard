package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;

import static de.eldoria.shepard.database.queries.PrefixData.setPrefix;
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
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper dataWrapper) {
        String cmd = args[0];
        if (cmd.equalsIgnoreCase("set") || cmd.equalsIgnoreCase("s")) {
            set(args, dataWrapper);
            return;
        }
        if (cmd.equalsIgnoreCase("reset") || cmd.equalsIgnoreCase("r")) {
            reset(dataWrapper);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, dataWrapper.getChannel());
        sendCommandUsage(dataWrapper.getChannel());
    }

    private void reset(MessageEventDataWrapper receivedEvent) {
        if (setPrefix(receivedEvent.getGuild(), ShepardBot.getConfig().getPrefix(), receivedEvent)) {
            MessageSender.sendMessage("Set Prefix to '" + ShepardBot.getConfig().getPrefix() + "'",
                    receivedEvent.getChannel());
        }
    }

    private void set(String[] args, MessageEventDataWrapper receivedEvent) {
        if (args.length == 1) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, receivedEvent.getChannel());
            sendCommandUsage(receivedEvent.getChannel());
            return;
        }

        if (args[1].length() > 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_PREFIX_LENGTH, receivedEvent.getChannel());
            return;
        }

        if (setPrefix(receivedEvent.getGuild(), args[1].trim(), receivedEvent)) {
            MessageSender.sendMessage("Changed prefix to '" + args[1].trim() + "'", receivedEvent.getChannel());
        }
    }
}
