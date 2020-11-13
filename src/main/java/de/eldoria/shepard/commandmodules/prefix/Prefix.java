package de.eldoria.shepard.commandmodules.prefix;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;

import javax.sql.DataSource;

import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.AD_PREFIX;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.A_PREFIX;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.C_RESET;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.C_SET;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.PrefixLocale.M_CHANGED;

/**
 * Command to change the bot prefix on a guild.
 */
@CommandUsage(EventContext.GUILD)
public class Prefix extends Command implements Executable, ReqConfig, ReqDataSource, ReqInit {

    private Config config;
    private DataSource source;
    private PrefixData prefixData;

    /**
     * Creates a new prefix command object.
     */
    public Prefix() {
        super("prefix",
                null,
                "command.prefix.description",
                SubCommand.builder("prefix")
                        .addSubcommand("command.prefix.subcommand.set",
                                Parameter.createCommand("set"),
                                Parameter.createInput("command.prefix.argument.prefix", "command.prefix.argumentDescription.prefix", true))
                        .addSubcommand("command.prefix.subcommand.reset",
                                Parameter.createCommand("reset")).build(),
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
    }

    private void reset(EventWrapper messageContext) {
        if (prefixData.setPrefix(messageContext.getGuild().get(), config.getGeneralSettings().getPrefix(), messageContext)) {
            MessageSender.sendMessage(M_CHANGED + " '" + config.getGeneralSettings().getPrefix() + "'",
                    messageContext.getMessageChannel());
        }
    }

    private void set(String[] args, EventWrapper messageContext) {
        if (args.length == 1) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext);
            return;
        }

        if (args[1].length() > 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_PREFIX_LENGTH, messageContext);
            return;
        }

        if (prefixData.setPrefix(messageContext.getGuild().get(), args[1].trim(), messageContext)) {
            MessageSender.sendMessage(M_CHANGED + " '" + args[1].trim() + "'", messageContext.getMessageChannel());
        }
    }

    @Override
    public void addConfig(Config config) {
        this.config = config;
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    @Override
    public void init() {
        prefixData = new PrefixData(source, config);
    }
}
