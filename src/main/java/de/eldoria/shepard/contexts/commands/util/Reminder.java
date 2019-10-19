package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class Reminder extends Command {
    public Reminder() {
        commandName = "remind";
        commandAliases = new String[] {"reminder"};
        commandDesc = "Set a reminder in a specified time.";
        commandArgs = new CommandArg[] {
                new CommandArg("action",
                        "**__a__dd** -> Create a new reminder." + lineSeparator()
                                + "**__r__emove** -> Remove a reminder" + lineSeparator()
                                + "**__s__how** -> Show your reminder.",
                        true),
                new CommandArg("value",
                        "**__a__dd** -> [message] [number] [min|hour|day]" + lineSeparator()
                                + "**__r__emove** -> [id]" + lineSeparator()
                                + "**__s__how** -> leave empty",
                        false
                )
        };
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        if (isArgument(cmd, "show", "s")) {

            return;
        }

        if (args.length < 4) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getChannel());
            return;
        }

        if (isArgument(cmd, "add", "a")) {
            int number;
            try {
                number = Integer.parseInt(args[args.length - 2]);
            } catch (NumberFormatException e) {
                MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext.getChannel());
                return;
            }
            if (!isArgument(args[args.length - 1], "min", "day", "days", "hour", "hours")) {
                MessageSender.sendSimpleError(ErrorType.INVALID_INTERVAL, messageContext.getChannel());
                return;
            }


        }

        i

    }
}
