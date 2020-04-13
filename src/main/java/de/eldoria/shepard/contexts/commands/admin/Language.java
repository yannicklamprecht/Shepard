package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.Parameter;
import de.eldoria.shepard.contexts.commands.argument.SubCommand;
import de.eldoria.shepard.database.queries.commands.LocaleData;
import de.eldoria.shepard.localization.util.LocaleCode;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.util.Arrays;

import static de.eldoria.shepard.localization.enums.commands.admin.LanguageLocale.AD_LANGUAGE_CODE_FORMAT;
import static de.eldoria.shepard.localization.enums.commands.admin.LanguageLocale.A_LANGUAGE_CODE_FORMAT;
import static de.eldoria.shepard.localization.enums.commands.admin.LanguageLocale.C_LIST;
import static de.eldoria.shepard.localization.enums.commands.admin.LanguageLocale.C_RESET;
import static de.eldoria.shepard.localization.enums.commands.admin.LanguageLocale.C_SET;
import static de.eldoria.shepard.localization.enums.commands.admin.LanguageLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.LanguageLocale.M_CHANGED;
import static de.eldoria.shepard.localization.enums.commands.admin.LanguageLocale.M_LIST;
import static de.eldoria.shepard.localization.enums.commands.admin.LanguageLocale.M_SUBMIT;

/**
 * Command to specify the bot config.
 * Languages are loaded from {@link de.eldoria.shepard.localization.LanguageHandler}
 */
public class Language extends Command {

    /**
     * Creates a new prefix command object.
     */
    public Language() {
        super("language",
                null,
                DESCRIPTION.tag,
                SubCommand.builder("language")
                        .addSubcommand(C_SET.tag,
                                Parameter.createCommand("set"),
                                Parameter.createInput(A_LANGUAGE_CODE_FORMAT.tag, AD_LANGUAGE_CODE_FORMAT.tag, true))
                        .addSubcommand(C_RESET.tag,
                                Parameter.createCommand("reset"))
                        .addSubcommand(C_LIST.tag,
                                Parameter.createCommand("list"))
                        .build(),
                ContextCategory.ADMIN);
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        if (isSubCommand(cmd, 0)) {
            set(args, messageContext);
            return;
        }
        if (isSubCommand(cmd, 1)) {
            reset(messageContext);
            return;
        }
        if (isSubCommand(cmd, 2)) {
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

        MessageSender.sendMessage(M_LIST + tableBuilder.toString() + "__" + M_SUBMIT + "__",
                messageContext.getTextChannel());
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
