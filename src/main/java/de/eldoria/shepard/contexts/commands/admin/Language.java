package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.LocaleData;
import de.eldoria.shepard.localization.util.LocaleCode;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.util.Arrays;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.*;
import static de.eldoria.shepard.localization.enums.commands.admin.LanguageLocale.*;

public class Language extends Command {

    /**
     * Creates a new prefix command object.
     */
    public Language() {
        commandName = "language";
        commandAliases = new String[] {"locale"};
        commandDesc = DESCRIPTION.tag;
        commandArgs = new CommandArg[] {
                new CommandArg("action", true,
                        new SubArg("set", C_SET.tag, true),
                        new SubArg("reset", C_RESET.tag, true),
                        new SubArg("list", C_LIST.tag, true)),
                new CommandArg("value", false,
                        new SubArg("set", A_LANGUAGE_CODE_FORMAT.tag),
                        new SubArg("reset", A_EMPTY.tag),
                        new SubArg("list", A_EMPTY.tag))
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
        if (arg.isSubCommand(cmd, 2)) {
            list(messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
    }

    private void list(MessageEventDataWrapper messageContext) {
        TextFormatting.TableBuilder tableBuilder = TextFormatting.getTableBuilder(Arrays.asList(LocaleCode.values()),
                "Language Name", "Language Code");
        for (LocaleCode code : LocaleCode.values()) {
            tableBuilder.next();
            tableBuilder.setRow(code.languageName, code.code);
        }

        MessageSender.sendMessage(M_LIST + tableBuilder.toString() + "__" + M_SUBMIT + "__"
                , messageContext.getTextChannel());
    }

    private void reset(MessageEventDataWrapper messageContext) {
        if (LocaleData.setLanguage(messageContext.getGuild(), LocaleCode.EN_US, messageContext)) {
            MessageSender.sendMessage(M_CHANGED + " '" + LocaleCode.EN_US + "'",
                    messageContext.getTextChannel());
        }
    }

    private void set(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 1) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            return;
        }
        LocaleCode localeCode = LocaleCode.parse(args[1]);
        if (localeCode == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_LOCALE_CODE, messageContext.getTextChannel());
            return;
        }

        if (LocaleData.setLanguage(messageContext.getGuild(), localeCode, messageContext)) {
            MessageSender.sendMessage(M_CHANGED + " `" + localeCode + "`", messageContext.getTextChannel());
        }
    }

}
