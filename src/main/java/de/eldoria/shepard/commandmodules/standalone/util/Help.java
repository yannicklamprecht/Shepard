package de.eldoria.shepard.commandmodules.standalone.util;

import de.eldoria.shepard.basemodules.commanddispatching.CommandHub;
import de.eldoria.shepard.basemodules.commanddispatching.util.ExecutionValidator;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.CommandUtil;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.prefix.PrefixData;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.localization.enums.commands.util.HelpLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqCommands;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqExecutionValidator;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_COMMAND_NAME;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_COMMAND_NAME;
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_INSUFFICIENT_PERMISSION;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

/**
 * A command for listing all possible commands.
 * Only Command which are allowed to be executed by this user on this guild are displayed.
 */
public class Help extends Command implements Executable, ReqCommands, ReqExecutionValidator, ReqDataSource,
        ReqInit, ReqConfig {

    private CommandHub commands;
    private ExecutionValidator validator;
    private DataSource source;
    private Config config;
    private PrefixData prefixData;

    /**
     * Creates new help command object.
     */
    public Help() {
        super("help",
                new String[] {"sendhelp"},
                HelpLocale.DESCRIPTION.tag,
                SubCommand.builder("help")
                        .addSubcommand(HelpLocale.A_COMMAND.tag,
                                Parameter.createInput(A_COMMAND_NAME.tag, AD_COMMAND_NAME.tag, false))
                        .build(),
                HelpLocale.DESCRIPTION.tag,
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        //Command List
        if (args.length == 0) {
            listCommands(messageContext);
            return;
        }

        Optional<Command> command = commands.getCommand(args[0]);
        if (command.isEmpty() || !validator.canAccess(command.get(), messageContext)) {
            MessageSender.sendSimpleError(ErrorType.COMMAND_NOT_FOUND, messageContext.getTextChannel());
            return;
        }

        String prefix = prefixData.getPrefix(messageContext.getGuild(), messageContext);
        //Command Help
        if (args.length == 1) {
            commandHelp(messageContext, command.get(), prefix);
            return;
        }

        MessageSender.sendMessage(localizeAllAndReplace(HelpLocale.M_USAGE.tag, messageContext.getGuild(),
                prefix), messageContext.getTextChannel());
    }

    /* Sends help for a specific command with description, alias and usage.*/
    private void commandHelp(MessageEventDataWrapper messageContext, Command command, String prefix) {
        if (validator.canAccess(command, messageContext)
                && validator.canUse(command, messageContext.getMember())) {
            MessageEmbed embed = CommandUtil.getCommandHelpEmbed(command, messageContext.getGuild(), prefix);
            messageContext.getTextChannel().sendMessage(embed).queue();
        } else {
            MessageSender.sendMessage(localizeAllAndReplace(M_INSUFFICIENT_PERMISSION.tag,
                    messageContext.getGuild(), "**" + command.getCommandName() + "**"),
                    messageContext.getTextChannel());
        }
    }

    /* Sends a list of all commands with description */
    private void listCommands(MessageEventDataWrapper messageContext) {
        Map<CommandCategory, List<Command>> commands = new HashMap<>();

        List<LocalizedField> fields = new ArrayList<>();

        for (Command command : this.commands.getCommands()) {
            if (!validator.displayInHelp(command, messageContext.getMember(), messageContext.getGuild(),
                    messageContext.getTextChannel())) {
                continue;
            }
            commands.putIfAbsent(command.getCategory(), new ArrayList<>());
            commands.get(command.getCategory()).add(command);
        }

        fields.add(getCommandField(commands, CommandCategory.BOT_CONFIG, messageContext));
        fields.add(getCommandField(commands, CommandCategory.ADMIN, messageContext));
        fields.add(getCommandField(commands, CommandCategory.EXCLUSIVE, messageContext));
        fields.add(getCommandField(commands, CommandCategory.FUN, messageContext));
        fields.add(getCommandField(commands, CommandCategory.UTIL, messageContext));
        fields.add(new LocalizedField("", localizeAllAndReplace(HelpLocale.M_LIST_COMMANDS.tag,
                messageContext.getGuild(),
                "`" + prefixData.getPrefix(messageContext.getGuild(), messageContext) + "<command> help`"),
                false, messageContext));
        fields.add(new LocalizedField(HelpLocale.M_MAYBE_USEFUL.tag,
                "**[" + HelpLocale.M_INVITE_ME + "]"
                        + "(https://discordapp.com/oauth2/authorize?client_id=512413049894731780&scope=bot&permissions=1544027254), "
                        + "[" + HelpLocale.M_SUPPORT_SERVER + "](https://discord.gg/AJyFGAj), "
                        + "[" + HelpLocale.M_FULL_COMMAND_LIST + "](https://www.shepardbot.de/commands), "
                        + "[" + HelpLocale.M_PERMISSION_HELP + "]"
                        + "(https://gitlab.com/shepardbot/ShepardBot/-/wikis/Commands/Permission-and-Command-Settings)**",
                false, messageContext));

        fields.removeIf(Objects::isNull);

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(messageContext)
                .setTitle("__**" + HelpLocale.M_COMMANDS + "**__");

        for (var field : fields) {
            builder.addField(field);
        }

        builder.setFooter(HelpLocale.M_CUSTOM_HELP_MESSAGE.tag);

        builder.setColor(Colors.Pastel.GREEN);

        messageContext.getTextChannel().sendMessage(builder.build()).queue();
    }

    private String getCommandNames(List<Command> commands) {
        return commands.stream().map(command -> "`" + command.getCommandName() + "`").collect(Collectors.joining(", "));
    }

    private LocalizedField getCommandField(Map<CommandCategory, List<Command>> commands, CommandCategory category,
                                           MessageEventDataWrapper messageContext) {
        List<Command> list = commands.getOrDefault(category, Collections.emptyList());
        if (!list.isEmpty()) {
            return new LocalizedField(category.categoryName,
                    getCommandNames(list), false, messageContext);
        }
        return null;
    }

    @Override
    public void addCommands(CommandHub commandHub) {
        this.commands = commandHub;
    }

    @Override
    public void addExecutionValidator(ExecutionValidator validator) {
        this.validator = validator;
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    @Override
    public void init() {
        prefixData = new PrefixData(source, config);
    }

    @Override
    public void addConfig(Config config) {
        this.config = config;
    }
}
