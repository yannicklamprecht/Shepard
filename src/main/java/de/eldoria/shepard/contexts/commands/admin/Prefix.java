package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.Parameter;
import de.eldoria.shepard.contexts.commands.argument.SubCommand;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import static de.eldoria.shepard.database.queries.commands.PrefixData.setPrefix;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.AD_PREFIX;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.A_PREFIX;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.C_RESET;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.C_SET;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.M_CHANGED;

/**
 * Command to change the bot prefix on a guild.
 */
public class Prefix extends Command {

    /**
     * Creates a new prefix command object.
     */
    public Prefix() {
        super("prefix",
                null,
                DESCRIPTION.tag,
                SubCommand.builder("prefix")
                        .addSubcommand(C_SET.tag,
                                Parameter.createCommand("set"),
                                Parameter.createInput(A_PREFIX.tag, AD_PREFIX.tag, true))
                        .addSubcommand(C_RESET.tag,
                                Parameter.createCommand("reset")).build(),
                ContextCategory.ADMIN);
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        SubCommand arg = subCommands[0];
        if (isSubCommand(cmd, 0)) {
            set(args, messageContext);
            return;
        }
        if (isSubCommand(cmd, 1)) {
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
