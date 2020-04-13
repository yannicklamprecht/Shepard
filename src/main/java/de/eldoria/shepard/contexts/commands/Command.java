package de.eldoria.shepard.contexts.commands;

import de.eldoria.shepard.C;
import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.collections.LatestCommandsCollection;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.ContextSensitive;
import de.eldoria.shepard.contexts.commands.argument.Parameter;
import de.eldoria.shepard.contexts.commands.argument.SubCommand;
import de.eldoria.shepard.contexts.commands.argument.SubCommandInfo;
import de.eldoria.shepard.database.queries.commands.PrefixData;
import de.eldoria.shepard.localization.LanguageHandler;
import de.eldoria.shepard.localization.enums.commands.CommandLocale;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.enums.commands.util.HelpLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import info.debatty.java.stringsimilarity.JaroWinkler;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.CommandLocale.*;
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_COMMAND_NOT_FOUND;
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_INSUFFICIENT_PERMISSION;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static java.lang.System.lineSeparator;

/**
 * An abstract class for commands.
 */
@Slf4j
public abstract class Command extends ContextSensitive {
    /**
     * Language handler instance.
     */
    protected final LanguageHandler locale;
    /**
     * Name of the command.
     */
    protected final String commandName;
    /**
     * Command aliase as string array.
     */
    protected final String[] commandAliases;
    /**
     * Description of command.
     */
    protected final String commandDesc;
    /**
     * Command args as command arg array.
     */
    protected final SubCommand[] subCommands;
    /**
     * True if the plugin works without a subcommand.
     */
    protected final boolean standalone;
    /**
     * Description if the plugin has a standalone function.
     */
    protected final String standaloneDescription;

    private final JaroWinkler similarity = new JaroWinkler();

    /**
     * Create a new command with a standalone function and register it to the {@link CommandCollection}.
     *
     * @param commandName           name of the command
     * @param commandAliases        command aliases
     * @param commandDesc           description of the command
     * @param subCommands           Subcommands of the command
     * @param standaloneDescription description of the standalone command
     * @param category              category of the command
     */
    protected Command(String commandName, String[] commandAliases, String commandDesc, SubCommand[] subCommands,
                      String standaloneDescription, ContextCategory category) {
        CommandCollection.getInstance().addCommand(this);
        locale = LanguageHandler.getInstance();
        this.commandName = commandName;
        this.commandAliases = commandAliases == null ? new String[0] : commandAliases;
        this.commandDesc = commandDesc;
        this.subCommands = subCommands == null ? new SubCommand[0] : subCommands;
        this.standalone = true;
        this.standaloneDescription = standaloneDescription;
        this.category = category;

        if (commandName == null || commandDesc == null || standaloneDescription == null) {
            throw new NullPointerException();
        }
        generateLazySubCommands();
    }

    /**
     * Create a new command without a standalone function and register it to the {@link CommandCollection}.
     *
     * @param commandName    name of the command
     * @param commandAliases command aliases
     * @param commandDesc    description of the command
     * @param subCommands    Subcommands of the command
     * @param category       category of the command
     */
    protected Command(String commandName, String[] commandAliases, String commandDesc, SubCommand[] subCommands,
                      ContextCategory category) {
        CommandCollection.getInstance().addCommand(this);
        locale = LanguageHandler.getInstance();
        this.commandName = commandName;
        this.commandAliases = commandAliases == null ? new String[0] : commandAliases;
        this.commandDesc = commandDesc;
        this.subCommands = subCommands == null ? new SubCommand[0] : subCommands;
        this.standalone = false;
        this.standaloneDescription = null;
        this.category = category;

        if (commandName == null || commandDesc == null) {
            throw new NullPointerException();
        }
        generateLazySubCommands();
    }

    /**
     * Create a new command with only a standalone function and register it to the {@link CommandCollection}.
     *
     * @param commandName    name of the command
     * @param commandAliases command aliases
     * @param commandDesc    description of the command
     * @param category       category of the command
     */
    protected Command(String commandName, String[] commandAliases, String commandDesc,
                      ContextCategory category) {
        CommandCollection.getInstance().addCommand(this);
        locale = LanguageHandler.getInstance();
        this.commandName = commandName;
        this.commandAliases = commandAliases == null ? new String[0] : commandAliases;
        this.commandDesc = commandDesc;
        this.subCommands = new SubCommand[0];
        this.standalone = true;
        this.standaloneDescription = null;
        this.category = category;

        if (commandName == null || commandDesc == null) {
            throw new NullPointerException();
        }
        generateLazySubCommands();
    }

    private void generateLazySubCommands() {
        // Lazy sub commands are unique in their parameter stage. but not in all sub commands
        // Iterate though each stage

        List<Parameter> parameters = new ArrayList<>();

        int maxParams = 0;
        for (var subCommand : subCommands) {
            maxParams = Math.max(maxParams, subCommand.getParameters().length);
        }

        for (int parameterStage = 0; parameterStage < maxParams; parameterStage++) {
            // Search commands in this parameter stage
            for (SubCommand s : subCommands) {
                Parameter[] p = s.getParameters();
                if (p.length > parameterStage) {
                    if (p[parameterStage].isCommand()) {
                        parameters.add(p[parameterStage]);
                    }
                }
            }

            // generate lazy commands
            int i = 0;
            do {
                for (Parameter p : parameters) {
                    p.setShortCommand(p.generateShortCommand(i));
                }
                i++;
                parameters = new ArrayList<>(getNotUniqueCommands(parameters));
            } while (parameters.size() != 0);
        }
    }

    private Set<Parameter> getNotUniqueCommands(List<Parameter> parameters) {
        Set<Parameter> p = new HashSet<>();

        for (Parameter param1 : parameters) {
            for (Parameter param2 : parameters) {
                if (param1.getCommandName().equals(param2.getCommandName())) continue;
                if (param1.getShortCommand().equals(param2.getShortCommand())) {
                    p.add(param2);
                }
            }
        }
        return p;
    }

    /**
     * Executes the command.
     *
     * @param label          Label/Alias which was used for command execution
     * @param args           Arguments of the command.
     * @param messageContext Message Received Event of the command execution
     */
    public final void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        //Check if the context can be used on guild by user
        if (!isContextValid(messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_COMMAND_NOT_FOUND.tag, messageContext.getGuild()),
                    messageContext.getTextChannel());
            return;
        }

        //check if the user has the permission on the guild
        if (!hasPermission(messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_INSUFFICIENT_PERMISSION.tag,
                    messageContext.getGuild(), "**" + getContextName() + "**"), messageContext.getTextChannel());
            return;
        }

        //Check if it is the help command
        if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
            sendCommandUsage(messageContext.getTextChannel());
            return;
        }

        //Check if the argument count is equal or more than the minimum arguments
        if (!checkArguments(args)) {
            try {
                MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
                return;
            } catch (InsufficientPermissionException ex) {
                MessageSender.handlePermissionException(ex, messageContext.getTextChannel());
                return;
            }
        }

        //Check if the command is on cooldown.
        int currentCooldown = CooldownManager.getInstance().getCurrentCooldown(
                this, messageContext.getGuild(), messageContext.getAuthor());
        if (currentCooldown != 0) {
            try {
                MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace(GeneralLocale.M_COOLDOWN.tag,
                        messageContext.getGuild(), currentCooldown + ""), messageContext.getTextChannel());
            } catch (InsufficientPermissionException ex) {
                MessageSender.handlePermissionException(ex, messageContext.getTextChannel());
            }
            return;
        }

        CooldownManager.getInstance().renewCooldown(this, messageContext.getGuild(), messageContext.getAuthor());

        MessageSender.logCommand(label, args, messageContext);

        try {
            internalExecute(label, args, messageContext);
        } catch (InsufficientPermissionException e) {
            MessageSender.handlePermissionException(e, messageContext.getTextChannel());

        } catch (RuntimeException e) {
            log.error(C.NOTIFY_ADMIN, "command execution failed", e);
            MessageSender.sendSimpleError(ErrorType.INTERNAL_ERROR, messageContext.getTextChannel());
            return;
        }

        LatestCommandsCollection.getInstance()
                .saveLatestCommand(messageContext.getGuild(), messageContext.getAuthor(),
                        this, label, args);
    }

    /**
     * Executes the method async.
     *
     * @param label          Label/Alias which was used for command execution
     * @param args           Arguments of the command.
     * @param messageContext Message Received Event of the command execution
     */
    public void executeAsync(String label, String[] args, MessageEventDataWrapper messageContext) {
        CompletableFuture.runAsync(() -> execute(label, args, messageContext));
    }

    /**
     * Internal executor for command. Called from inside the class.
     *
     * @param label          Label/Alias which was used for command execution
     * @param args           Arguments of the command.
     * @param messageContext Message Received Event of the command execution
     */
    protected abstract void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext);

    /**
     * Get the name of the command.
     *
     * @return the name of the command.
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Get the description of the command.
     *
     * @return the description
     */
    public String getCommandDesc() {
        return commandDesc;
    }


    /**
     * Get all possible arguments for the command.
     *
     * @return an array of command arguments.
     */
    public SubCommand[] getSubCommands() {
        return subCommands;
    }

    /**
     * Get possible aliases of a command.
     *
     * @return an array of aliases.
     */
    public String[] getCommandAliases() {
        return commandAliases;
    }

    /**
     * Check whether a string is a valid command or not.
     *
     * @param command the string to check.
     * @return {@code true} if the command matched, {@code false} otherwise.
     */
    public boolean isCommand(String command) {
        if (command.equalsIgnoreCase(this.commandName) || command.equalsIgnoreCase(getClass().getSimpleName())) {
            return true;
        }

        if (this.commandAliases != null) {
            for (String alias : this.commandAliases) {
                if (command.equalsIgnoreCase(alias)) return true;
            }
        }
        return false;
    }

    /**
     * Checks if enough arguments are present for the comment.
     *
     * @param args string arg array
     * @return true if enough arguments are present
     */
    public boolean checkArguments(String[] args) {
        // Check if command is standalone
        if (standalone) return true;

        // search in subcommands
        for (var command : subCommands) {
            if (command.matchArgs(args)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Send the usage of the command to a channel.
     *
     * @param channel Channel where the usage should be send in.
     */
    public void sendCommandUsage(TextChannel channel) {
        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(channel.getGuild());
        String prefix = PrefixData.getPrefix(channel.getGuild(), null);

        builder.setTitle("__**" + HelpLocale.M_HELP_FOR_COMMAND + " " + getCommandName() + "**__")
                .setColor(Color.green);

        builder.setDescription(getCommandDesc());

        // Build alias field
        if (getCommandAliases() != null && getCommandAliases().length != 0) {
            builder.appendDescription(lineSeparator() + "__**" + HelpLocale.W_ALIASES + ":**__ "
                    + String.join(", ", getCommandAliases()));
        }

        // Build main command field. Only present when command has a standalone function and subcommands.
        if (standalone && subCommands.length != 0) {
            // TODO: Add locale codes
            builder.addField("**__" + BASE_COMMAND.tag + "__**:",
                    "**" + prefix + commandName + "**\n" + standaloneDescription, false);
        }

        // Build subcommand field.
        if (subCommands.length != 0) {
            List<String> subcommandHelp = getSubcommandHelp().stream()
                    .map(s -> TextLocalizer.localizeAll(s, channel).replace("\n", "\n> "))
                    .collect(Collectors.toList());
            List<String> chunks = new ArrayList<>();

            StringBuilder sBuilder = new StringBuilder();
            for (var s : subcommandHelp) {
                if (sBuilder.length() + s.length() > 1024) {
                    chunks.add(sBuilder.toString());
                    sBuilder.setLength(0);
                }
                sBuilder.append(s).append("\n\n");
            }
            if (builder.length() != 0) {
                chunks.add(sBuilder.toString());
            }

            for (var c : chunks) {
                builder.addField("",
                        c.replace("{prefix}", prefix), false);
            }
        }

        channel.sendMessage(builder.build()).queue();
    }

    /**
     * Get the highest similarity score between command string and command name and aliases.
     *
     * @param command command to check
     * @return score between 0 and 1
     */
    public double getSimilarityScore(String command) {
        String lowerCommand = command.toLowerCase();
        double cmdScore = similarity.similarity(commandName.toLowerCase(),
                lowerCommand);

        for (String alias : commandAliases) {
            double similarity = this.similarity.similarity(alias.toLowerCase(), lowerCommand);
            cmdScore = Math.max(cmdScore, similarity);
        }
        return cmdScore;
    }

    /**
     * Get a object which holds all information about the command.
     *
     * @return command info object
     */
    public CommandInfo getCommandInfo() {
        SubCommandInfo[] sc = new SubCommandInfo[subCommands.length];
        return new CommandInfo(getContextName(),
                commandName,
                commandAliases,
                commandDesc,
                standaloneDescription,
                category,
                Arrays.stream(subCommands)
                        .map(SubCommand::getSubCommandInfo)
                        .collect(Collectors.toList()).toArray(sc));
    }

    /**
     * Get the subcommand help.
     *
     * @return subcommand help as preformatted string.
     */
    public List<String> getSubcommandHelp() {
        List<String> subCommandsHelp = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            subCommandsHelp.add(subCommand.getCommandPattern());
        }
        return subCommandsHelp;
    }

    /**
     * Check if the first sub command matches a string.
     *
     * @param cmd command to check
     * @param i   subcommand index to check
     * @return true if the subcommand matches.
     */
    protected boolean isSubCommand(String cmd, int i) {
        return subCommands[i].isSubCommand(cmd);
    }

}
