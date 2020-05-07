package de.eldoria.shepard.basemodules.commanddispatching;

import de.eldoria.shepard.C;
import de.eldoria.shepard.basemodules.commanddispatching.util.CommandDispatchingError;
import de.eldoria.shepard.basemodules.commanddispatching.util.ExecutionValidator;
import de.eldoria.shepard.basemodules.commanddispatching.util.UnkownCommandDispachingMethod;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.CommandUtil;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.prefix.PrefixData;
import de.eldoria.shepard.commandmodules.repeatcommand.LatestCommandsCollection;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqCooldownManager;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqExecutionValidator;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqLatestCommands;
import de.eldoria.shepard.webapi.apiobjects.commandserialization.CommandInfos;
import de.eldoria.shepard.webapi.apiobjects.commandserialization.FullCommandInfo;
import de.eldoria.shepard.webapi.apiobjects.commandserialization.SimpleCommandInfo;
import de.eldoria.shepard.webapi.apiobjects.commandserialization.SimpleCommandInfos;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import info.debatty.java.stringsimilarity.JaroWinkler;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_COMMAND_DISABLED;
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_COMMAND_DISABLED_IN_CHANNEL;
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_COMMAND_NOT_FOUND;
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_INSUFFICIENT_PERMISSION;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

/**
 * The command collection holds every registered and active {@link Command} object.
 * Provides a method {@link #getCommand(String)} to find a command by a alias or command name.
 * It can also find a Command by similarity from a string ({@link #getSimilarCommands(String)}).
 * It provides information about every command by the {@link #getCommandInfos(CommandCategory...)} method
 */
@Slf4j
public final class CommandHub implements ReqConfig, ReqLatestCommands, ReqExecutionValidator, ReqCooldownManager,
        ReqDataSource, ReqInit {
    private final List<Command> commands = new ArrayList<>();
    private final JaroWinkler similarity = new JaroWinkler();
    private ThreadPoolExecutor threads;
    private Config config;
    private ExecutionValidator validator;
    private LatestCommandsCollection latestCommands;
    private CooldownManager cooldownManager;
    private PrefixData prefixData;
    private DataSource source;

    /**
     * Create a new command hub instance.
     */
    public CommandHub() {
    }

    /**
     * Add commands to the command Collection.
     *
     * @param commands Command objects to add.
     */
    public void addCommand(Command... commands) {
        this.commands.addAll(Arrays.asList(commands));
    }


    /**
     * Get a list of all registered commands.
     *
     * @return unmodifiable list
     */
    public List<Command> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    /**
     * Dispatch a command to the command hub.
     * The command will only be executed it the user has access and the permission to execute the command.
     *
     * @param command command to execute
     * @param label label of command
     * @param args arguments of command
     * @param messageContext message event data
     */
    public void dispatchCommand(Command command, String label, String[] args, MessageEventDataWrapper messageContext) {
        //Check if the context can be used on guild by user
        if (!validator.canAccess(command, messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_COMMAND_NOT_FOUND.tag, messageContext.getGuild()),
                    messageContext.getTextChannel());
            return;
        }

        Optional<SubCommand> matchingSubcommand = command.getMatchingSubcommand(args);
        String permission;
        permission = matchingSubcommand.map(subCommand -> command.getCommandIdentifier() + "."
                + subCommand.getSubCommandIdentifier()).orElseGet(command::getCommandIdentifier);

        //check if the user has the permission on the guild
        if (!validator.canUse(permission, messageContext.getMember())) {
            MessageSender.sendMessage(localizeAllAndReplace(M_INSUFFICIENT_PERMISSION.tag,
                    messageContext.getGuild(), "**" + permission + "**"),
                    messageContext.getTextChannel());
            return;
        }

        if (!validator.isCommandEnabled(command, messageContext.getGuild())) {
            messageContext.getTextChannel()
                    .sendMessage(localizeAllAndReplace("**" + M_COMMAND_DISABLED.tag + "**",
                            messageContext.getGuild(), command.getCommandIdentifier())).queue();
            return;
        }

        // Check if command can be used in channel
        if (!validator.canUseInChannel(command, messageContext.getMember(),
                messageContext.getGuild(), messageContext.getTextChannel())) {
            messageContext.getTextChannel()
                    .sendMessage(localizeAllAndReplace("**" + M_COMMAND_DISABLED_IN_CHANNEL.tag + "**",
                            messageContext.getGuild(), command.getCommandIdentifier())).queue();
            return;
        }


        //Check if it is the help command
        if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
            sendHelpText(command, messageContext.getTextChannel());
            return;
        }

        //Check if the arguments match the main command or one of the sub commands.
        if (!command.checkArguments(args)) {
            try {
                MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
                return;
            } catch (InsufficientPermissionException ex) {
                MessageSender.handlePermissionException(config, ex, messageContext.getTextChannel());
                return;
            }
        }

        if (handleCooldown(command, messageContext)) {
            return;
        }


        MessageSender.logCommand(label, args, messageContext);

        runCommand(command, label, args, messageContext);

        cooldownManager.renewCooldown(command, messageContext.getGuild(), messageContext.getAuthor());
        latestCommands.saveLatestCommand(messageContext.getGuild(), messageContext.getAuthor(), command, label, args);
    }

    private boolean handleCooldown(Command command, MessageEventDataWrapper messageContext) {
        int currentCooldown = cooldownManager.getCurrentCooldown(
                command, messageContext.getGuild(), messageContext.getAuthor());
        if (currentCooldown != 0) {
            try {
                MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace(GeneralLocale.M_COOLDOWN.tag,
                        messageContext.getGuild(), currentCooldown + ""), messageContext.getTextChannel());
            } catch (InsufficientPermissionException ex) {
                MessageSender.handlePermissionException(config, ex, messageContext.getTextChannel());
            }
            return true;
        }
        return false;
    }

    private void sendHelpText(Command command, TextChannel channel) {
        String prefix = prefixData.getPrefix(channel.getGuild(), null);

        MessageEmbed commandHelpEmbed = CommandUtil.getCommandHelpEmbed(command, channel.getGuild(), prefix);

        channel.sendMessage(commandHelpEmbed).queue();
    }

    /**
     * Get the subcommand help.
     *
     * @param subCommands subcommand to process.
     * @return subcommand help as preformatted string.
     */
    public List<String> getSubcommandHelp(SubCommand[] subCommands) {
        List<String> subCommandsHelp = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            subCommandsHelp.add(subCommand.getCommandPattern());
        }
        return subCommandsHelp;
    }


    /**
     * run a command.
     *
     * @param command        command to execute
     * @param label          label of command
     * @param args           argument of command
     * @param messageContext message context.
     */
    private void runCommand(Command command, String label, String[] args, MessageEventDataWrapper messageContext) {
        if (command instanceof ExecutableAsync) {
            CommandDispatchingError error = new CommandDispatchingError();
            if (threads.getActiveCount() >= config.getGeneralSettings().getCommandExecutionThreads() - 2) {
                log.warn(C.NOTIFY_ADMIN, "Threads for command execution are running low!");
            }
            log.debug("Threads active in pool: {}/{} of max {}", threads.getActiveCount(), threads.getPoolSize(),
                    config.getGeneralSettings().getCommandExecutionThreads());
            threads.execute(() -> {
                try {
                    ((ExecutableAsync) command).execute(label, args, messageContext);
                } catch (InsufficientPermissionException e) {
                    MessageSender.handlePermissionException(config, e, messageContext.getTextChannel());
                } catch (RuntimeException e) {
                    log.error(C.NOTIFY_ADMIN, "command execution failed: " + label + " " + String.join(" ", args), e);
                    log.error(C.NOTIFY_ADMIN, "Caused by", error);
                    MessageSender.sendSimpleError(ErrorType.INTERNAL_ERROR, messageContext.getTextChannel());
                }
            });
            return;
        } else if (command instanceof Executable) {
            try {
                ((Executable) command).execute(label, args, messageContext);
            } catch (InsufficientPermissionException e) {
                MessageSender.handlePermissionException(config, e, messageContext.getTextChannel());
            } catch (RuntimeException e) {
                log.error(C.NOTIFY_ADMIN, "command execution failed: " + label + " "
                        + String.join(" ", args), label, String.join(" ", args), e);
                MessageSender.sendSimpleError(ErrorType.INTERNAL_ERROR, messageContext.getTextChannel());
            }
            return;
        }
        throw new UnkownCommandDispachingMethod(command);
    }


    /**
     * Get a command object by command name or alias.
     *
     * @param command command name or alias
     * @return Command object or null if no command was found.
     */
    public Optional<Command> getCommand(String command) {
        for (Command currentCommand : commands) {
            if (currentCommand.isCommand(command)) {
                return Optional.of(currentCommand);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns a command which have the most similar name or alias.
     *
     * @param command command entered.
     * @return Command or null if no command was found which was similar enough.
     */
    public List<Command> getSimilarCommands(String command) {
        List<RankedCommand> rankedCommands = new ArrayList<>();
        for (Command currentCommand : commands) {
            double similarityScore = getSimilarityScore(command, currentCommand);
            if (similarityScore > 0.75) {
                rankedCommands.add(new RankedCommand(similarityScore, currentCommand));
            }
        }

        rankedCommands.sort(Collections.reverseOrder());
        return rankedCommands.stream().map(rankedCommand -> rankedCommand.command).collect(Collectors.toList());
    }

    /**
     * Get the highest similarity score between command string and command name and aliases.
     *
     * @param command command to check
     * @return score between 0 and 1
     */
    private double getSimilarityScore(String name, Command command) {
        String lowerCommand = name.toLowerCase();
        double cmdScore = similarity.similarity(command.getCommandName().toLowerCase(),
                lowerCommand);

        for (String alias : command.getCommandAliases()) {
            double similarity = this.similarity.similarity(alias.toLowerCase(), lowerCommand);
            cmdScore = Math.max(cmdScore, similarity);
        }
        return cmdScore;
    }


    /**
     * Get a objects which holds information for all Commands.
     *
     * @param excludes command types, which should be excluded.
     * @return CommandInfos object
     */
    public CommandInfos getCommandInfos(CommandCategory... excludes) {
        List<CommandCategory> excludeList = Arrays.asList(excludes);
        List<FullCommandInfo> collect = commands.stream().map(Command::getCommandInfo)
                .filter(fullCommandInfo ->
                        !excludeList.contains(fullCommandInfo.getCategory())).collect(Collectors.toList());
        return new CommandInfos(collect);
    }

    /**
     * Get a objects which holds information for all Commands.
     *
     * @param excludes command types, which should be excluded.
     * @return CommandInfos object
     */
    public SimpleCommandInfos getSimpleCommandInfos(CommandCategory... excludes) {
        List<CommandCategory> excludeList = Arrays.asList(excludes);
        List<SimpleCommandInfo> collect = commands.stream().filter(command ->
                !excludeList.contains(command.getCategory()))
                .map(SimpleCommandInfo::new).collect(Collectors.toList());
        return new SimpleCommandInfos(collect);
    }

    @Override
    public void addConfig(Config config) {
        this.config = config;
    }

    @Override
    public void addExecutionValidator(ExecutionValidator validator) {

        this.validator = validator;
    }

    @Override
    public void addLatestCommand(LatestCommandsCollection latestCommands) {
        this.latestCommands = latestCommands;
    }

    @Override
    public void addCooldownManager(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    @Override
    public void init() {
        prefixData = new PrefixData(source, config);
        threads = (ThreadPoolExecutor) Executors.newFixedThreadPool(
                config.getGeneralSettings().getCommandExecutionThreads());

    }

    static class RankedCommand implements Comparable<RankedCommand> {
        /**
         * Rank of the command.
         */
        final double rank;
        /**
         * Command object which is ranked.
         */
        final Command command;

        /**
         * Creates a new ranked command.
         *
         * @param rank    rank of command
         * @param command command
         */
        RankedCommand(double rank, Command command) {
            this.rank = rank;
            this.command = command;
        }

        @Override
        public int compareTo(@NotNull CommandHub.RankedCommand cmd) {
            return Double.compare(rank, cmd.rank);
        }
    }

}