package de.eldoria.shepard.commandmodules.language;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.util.LocaleCode;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;

import javax.sql.DataSource;
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
// TODO: Allow user to set own language to override global or guild settings.
@CommandUsage(EventContext.GUILD)
public class Language extends Command implements Executable, ReqDataSource {

    private LocaleData localeData;

    /**
     * Creates a new prefix command object.
     */
    public Language() {
        super("language",
                null,
                "command.language.description",
                SubCommand.builder("language")
                        .addSubcommand("command.language.subcommand.set",
                                Parameter.createCommand("set"),
                                Parameter.createInput("command.language.argument.languageCodeFormat", "command.language.argumentDescription.languageCodeFormat", true))
                        .addSubcommand("command.language.subcommand.reset",
                                Parameter.createCommand("reset"))
                        .addSubcommand("command.language.subcommand.list",
                                Parameter.createCommand("list"))
                        .build(),
                CommandCategory.ADMIN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String cmd = args[0];
        if (isSubCommand(cmd, 0)) {
            set(args, wrapper);
            return;
        }
        if (isSubCommand(cmd, 1)) {
            reset(wrapper);
            return;
        }
        if (isSubCommand(cmd, 2)) {
            list(wrapper);
            return;
        }
    }

    private void list(EventWrapper wrapper) {
        TextFormatting.TableBuilder tableBuilder = TextFormatting.getTableBuilder(Arrays.asList(LocaleCode.values()),
                "Language Name", "Language Code");
        for (LocaleCode code : LocaleCode.values()) {
            tableBuilder.next();
            tableBuilder.setRow(code.languageName, code.code);
        }

        MessageSender.sendMessage(M_LIST + tableBuilder.toString() + "__" + M_SUBMIT + "__",
                wrapper.getMessageChannel());
    }

    private void reset(EventWrapper wrapper) {
        if (localeData.setLanguage(wrapper.getGuild().get(), LocaleCode.EN_US, wrapper)) {
            MessageSender.sendMessage(M_CHANGED + " '" + LocaleCode.EN_US + "'",
                    wrapper.getMessageChannel());
        }
    }

    private void set(String[] args, EventWrapper wrapper) {
        if (args.length == 1) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, wrapper);
            return;
        }
        LocaleCode localeCode = LocaleCode.parse(args[1]);
        if (localeCode == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_LOCALE_CODE, wrapper);
            return;
        }

        if (localeData.setLanguage(wrapper.getGuild().get(), localeCode, wrapper)) {
            MessageSender.sendMessage(M_CHANGED + " `" + localeCode + "`", wrapper.getMessageChannel());
        }
    }

    @Override
    public void addDataSource(DataSource source) {
        localeData = new LocaleData(source);
    }
}
