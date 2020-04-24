package de.eldoria.shepard.basemodules.commanddispatching;

import de.eldoria.shepard.C;
import de.eldoria.shepard.basemodules.commanddispatching.util.CommandDispatchingError;
import de.eldoria.shepard.basemodules.commanddispatching.util.UnkownCommandDispachingMethod;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.webapi.apiobjects.commandserialization.CommandInfos;
import de.eldoria.shepard.webapi.apiobjects.commandserialization.FullCommandInfo;
import de.eldoria.shepard.webapi.apiobjects.commandserialization.SimpleCommandInfo;
import de.eldoria.shepard.webapi.apiobjects.commandserialization.SimpleCommandInfos;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import info.debatty.java.stringsimilarity.JaroWinkler;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * The command collection holds every registered and active {@link Command} object.
 * Provides a method {@link #getCommand(String)} to find a command by a alias or command name.
 * It can also find a Command by similarity from a string ({@link #getSimilarCommands(String)}).
 * It provides information about every command by the {@link #getCommandInfos(CommandCategory...)} method
 */
@Slf4j
public final class CommandHub implements ReqConfig {
    private final List<Command> commands = new ArrayList<>();
    private final JaroWinkler similarity = new JaroWinkler();
    private final ThreadPoolExecutor threads = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    private Config config;

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
     * run a command.
     *
     * @param command        command to execute
     * @param label          label of command
     * @param args           argument of command
     * @param messageContext message context.
     */
    public void runCommand(Command command, String label, String[] args, MessageEventDataWrapper messageContext) {
        if (command instanceof ExecutableAsync) {
            CommandDispatchingError error = new CommandDispatchingError();
            if (threads.getActiveCount() >= 18) {
                log.warn(C.NOTIFY_ADMIN, "Threads for command execution are running low!");
            }
            log.debug("Threads active in pool: {} of {}", threads.getActiveCount(), threads.getPoolSize());
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
                log.error(C.NOTIFY_ADMIN, "command execution failed: " + label + " " + String.join(" ", args), label, String.join(" ", args), e);
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
