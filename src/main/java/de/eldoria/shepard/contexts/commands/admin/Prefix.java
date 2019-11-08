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
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_EMPTY;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.A_PREFIX_FORMAT;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.C_RESET;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.C_SET;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.M_CHANGED;

public class Prefix extends Command {

    /**
     * Creates a new prefix command object.
     */
    public Prefix() {
        commandName = "prefix";
        commandDesc = DESCRIPTION.tag;
        commandArgs = new CommandArg[] {
                new CommandArg("action", true,
                        new SubArg("set", C_SET.tag, true),
                        new SubArg("reset", C_RESET.tag, true)),
                new CommandArg("value", false,
                        new SubArg("set", A_PREFIX_FORMAT.tag),
                        new SubArg("reset", A_EMPTY.tag))
        };
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        CommandArg arg = commandArgs[0];
        if (arg.isSubCommand(cmd, 0)) {
            set(args, messageContext);
            return;
        }
        if (arg.isSubCommand(cmd, 1)) {
            reset(messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
    }

    private void reset(MessageEventDataWrapper messageContext) {
        if (setPrefix(messageContext.getGuild(), ShepardBot.getConfig().getPrefix(), messageContext)) {
            MessageSender.sendMessage(M_CHANGED + " '" + ShepardBot.getConfig().getPrefix() + "'",
                    messageContext.getTextChannel());
        }
    }

    private void set(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 1) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            return;
        }

        if (args[1].length() > 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_PREFIX_LENGTH, messageContext.getTextChannel());
            return;
        }

        if (setPrefix(messageContext.getGuild(), args[1].trim(), messageContext)) {
            MessageSender.sendMessage(M_CHANGED + " '" + args[1].trim() + "'", messageContext.getTextChannel());
        }
    }
}
