package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArgument;
import de.eldoria.shepard.contexts.commands.argument.SubArgument;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import static de.eldoria.shepard.database.queries.PrefixData.setPrefix;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_EMPTY;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.*;

/**
 * Command to change the bot prefix on a guild.
 */
public class Prefix extends Command {

    /**
     * Creates a new prefix command object.
     */
    public Prefix() {
        commandName = "prefix";
        commandDesc = DESCRIPTION.tag;
        commandArguments = new CommandArgument[] {
                new CommandArgument("action", true,
                        new SubArgument("set", C_SET.tag, true),
                        new SubArgument("reset", C_RESET.tag, true)),
                new CommandArgument("value", false,
                        new SubArgument("set", A_PREFIX_FORMAT.tag),
                        new SubArgument("reset", A_EMPTY.tag))
        };
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        CommandArgument arg = commandArguments[0];
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
