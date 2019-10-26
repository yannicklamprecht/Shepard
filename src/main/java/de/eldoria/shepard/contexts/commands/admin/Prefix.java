package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;

import static de.eldoria.shepard.database.queries.PrefixData.setPrefix;
import static de.eldoria.shepard.localization.enums.GeneralLocale.A_EMPTY;
import static de.eldoria.shepard.localization.enums.admin.PrefixLocale.*;
import static de.eldoria.shepard.util.Verifier.isArgument;

public class Prefix extends Command {

    /**
     * Creates a new prefix command object.
     */
    public Prefix() {
        commandName = "prefix";
        commandDesc = "Manage prefix settings";
        commandArgs = new CommandArg[] {
                new CommandArg("action", true,
                        new SubArg("set", C_SET.replacement, true),
                        new SubArg("reset", C_RESET.replacement, true)),
                new CommandArg("value", false,
                        new SubArg("set", A_PREFIX_FORMAT.replacement),
                        new SubArg("reset", A_EMPTY.replacement))
        };
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

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext);
    }

    private void reset(MessageEventDataWrapper messageContext) {
        if (setPrefix(messageContext.getGuild(), ShepardBot.getConfig().getPrefix(), messageContext)) {
            MessageSender.sendMessage(M_CHANGED + " '" + ShepardBot.getConfig().getPrefix() + "'",
                    messageContext);
        }
    }

    private void set(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 1) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext);
            return;
        }

        if (args[1].length() > 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_PREFIX_LENGTH, messageContext);
            return;
        }

        if (setPrefix(messageContext.getGuild(), args[1].trim(), messageContext)) {
            MessageSender.sendMessage(M_CHANGED + " '" + args[1].trim() + "'", messageContext);
        }
    }
}
