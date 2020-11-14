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
import de.eldoria.shepard.modulebuilder.requirements.*;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

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
                new String[]{"sendhelp"},
                "command.help.description",
                SubCommand.builder("help")
                        .addSubcommand("command.help.subcommand.command",
                                Parameter.createInput("command.general.argument.contextName", "command.general.argumentDescription.contextName", false))
                        .build(),
                "command.help.description",
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        //Command List
        if (args.length == 0) {
            listCommands(wrapper);
            return;
        }

        Optional<Command> command = commands.getCommand(args[0]);
        if (command.isEmpty() || !validator.canAccess(command.get(), wrapper)) {
            MessageSender.sendSimpleError(ErrorType.COMMAND_NOT_FOUND, wrapper);
            return;
        }

        String prefix = prefixData.getPrefix(wrapper.getGuild().get());
        //Command Help
        if (args.length == 1) {
            commandHelp(wrapper, command.get(), prefix);
            return;
        }

        MessageSender.sendMessage(localizeAllAndReplace(HelpLocale.M_USAGE.tag, wrapper,
                prefix), wrapper.getMessageChannel());
    }

    /* Sends help for a specific command with description, alias and usage.*/
    private void commandHelp(EventWrapper wrapper, Command command, String prefix) {
        if (validator.canAccess(command, wrapper)
                && validator.canUse(command, wrapper)) {
            MessageEmbed embed = CommandUtil.getCommandHelpEmbed(command, wrapper, prefix);
            wrapper.getMessageChannel().sendMessage(embed).queue();
        } else {
            MessageSender.sendMessage(localizeAllAndReplace(M_INSUFFICIENT_PERMISSION.tag,
                    wrapper, "**" + command.getCommandName() + "**"),
                    wrapper.getMessageChannel());
        }
    }

    /* Sends a list of all commands with description */
    private void listCommands(EventWrapper wrapper) {
        Map<CommandCategory, List<Command>> commands = new HashMap<>();

        List<LocalizedField> fields = new ArrayList<>();

        for (Command command : this.commands.getCommands()) {
            if (!validator.displayInHelp(command, wrapper)) {
                continue;
            }
            commands.putIfAbsent(command.getCategory(), new ArrayList<>());
            commands.get(command.getCategory()).add(command);
        }

        fields.add(getCommandField(commands, CommandCategory.BOT_CONFIG, wrapper));
        fields.add(getCommandField(commands, CommandCategory.ADMIN, wrapper));
        fields.add(getCommandField(commands, CommandCategory.MODERATION, wrapper));
        fields.add(getCommandField(commands, CommandCategory.EXCLUSIVE, wrapper));
        fields.add(getCommandField(commands, CommandCategory.FUN, wrapper));
        fields.add(getCommandField(commands, CommandCategory.REACTION, wrapper));
        fields.add(getCommandField(commands, CommandCategory.UTIL, wrapper));
        fields.add(new LocalizedField("", localizeAllAndReplace(HelpLocale.M_LIST_COMMANDS.tag,
                wrapper,
                "`" + prefixData.getPrefix(wrapper) + "<command> help`"),
                false, wrapper));
        fields.add(new LocalizedField(HelpLocale.M_MAYBE_USEFUL.tag,
                "**[" + HelpLocale.M_INVITE_ME + "]"
                        + "(https://invite.shepardbot.de/), "
                        + "[" + HelpLocale.M_SUPPORT_SERVER + "](https://discord.gg/AJyFGAj), "
                        + "[" + HelpLocale.M_FULL_COMMAND_LIST + "](https://www.shepardbot.de/commands), "
                        + "[" + HelpLocale.M_PERMISSION_HELP + "]"
                        + "(https://gitlab.com/shepardbot/ShepardBot/-/wikis/Commands/Permission-and-Command-Settings)**",
                false, wrapper));

        fields.removeIf(Objects::isNull);

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(wrapper)
                .setTitle("__**" + HelpLocale.M_COMMANDS + "**__");

        for (var field : fields) {
            builder.addField(field);
        }

        builder.setFooter(HelpLocale.M_CUSTOM_HELP_MESSAGE.tag);

        builder.setColor(Colors.Pastel.GREEN);

        wrapper.getMessageChannel().sendMessage(builder.build()).queue();
    }

    private String getCommandNames(List<Command> commands) {
        return commands.stream()
                .map(command -> "`" + command.getCommandName()
                        + command.getCommandAliasesHelp() + "`")
                .collect(Collectors.joining(", "));
    }

    private LocalizedField getCommandField(Map<CommandCategory, List<Command>> commands, CommandCategory category,
                                           EventWrapper wrapper) {
        List<Command> list = commands.getOrDefault(category, Collections.emptyList());
        if (!list.isEmpty()) {
            return new LocalizedField(category.categoryName,
                    getCommandNames(list), false, wrapper);
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
