package de.eldoria.shepard.contexts.commands;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.collections.LatestCommandsCollection;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.database.queries.PrefixData;
import de.eldoria.shepard.localization.LanguageHandler;
import de.eldoria.shepard.localization.enums.commands.util.HelpLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.ContextSensitive;
import info.debatty.java.stringsimilarity.JaroWinkler;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.awt.Color;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;

/**
 * An abstract class for commands.
 */
public abstract class Command extends ContextSensitive {
    /**
     * Language handler instance.
     */
    protected LanguageHandler locale;
    /**
     * Name of the command.
     */
    protected String commandName = "";
    /**
     * Command aliase as string array.
     */
    protected String[] commandAliases = new String[0];
    /**
     * Description of command.
     */
    protected String commandDesc = "";
    /**
     * Command args as command arg array.
     */
    protected CommandArg[] commandArgs = new CommandArg[0];

    private final JaroWinkler similarity = new JaroWinkler();

    /**
     * Create a new command an register it to the {@link CommandCollection}.
     */
    protected Command() {
        CommandCollection.getInstance().addCommand(this);
        locale = LanguageHandler.getInstance();
    }

    /**
     * Executes the command.
     *
     * @param label          Label/Alias which was used for command execution
     * @param args           Arguments of the command.
     * @param messageContext Message Received Event of the command execution
     */
    @Deprecated
    public final void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        try {
            internalExecute(label, args, messageContext);
        } catch (InsufficientPermissionException e) {
            messageContext.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel ->
                    MessageSender.handlePermissionException(e, messageContext.getTextChannel()));
        } catch (Throwable e) {
            ShepardBot.getLogger().error(e);
            MessageSender.sendSimpleError(ErrorType.INTERNAL_ERROR, messageContext.getTextChannel());
            return;
        }
        MessageSender.logCommand(label, args, messageContext);
        LatestCommandsCollection.getInstance().saveLatestCommand(messageContext.getGuild(), messageContext.getAuthor(),
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
    private CommandArg[] getCommandArgs() {
        if (commandArgs == null) {
            commandArgs = new CommandArg[0];
        }
        return commandArgs;
    }

    /**
     * Get possible aliases of a command.
     *
     * @return an array of aliases.
     */
    private String[] getCommandAliases() {
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
        int requiredArguments = 0;
        for (CommandArg a : commandArgs) {
            if (a.isRequired()) {
                requiredArguments++;
            }
        }
        return args.length >= requiredArguments;
    }

    /**
     * Send the usage of the command to a channel.
     *
     * @param channel Channel where the usage should be send in.
     */
    public void sendCommandUsage(TextChannel channel) {
        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(channel.getGuild());

        builder.setDescription(getCommandDesc());

        // Set aliases
        if (getCommandAliases() != null && getCommandAliases().length != 0) {
            builder.appendDescription(lineSeparator() + "__**" + HelpLocale.W_ALIASES + ":**__ "
                    + String.join(", ", getCommandAliases()));
        }

        String args = Arrays.stream(getCommandArgs()).map(CommandArg::getHelpString)
                .collect(Collectors.joining(" "));


        builder.addField(new LocalizedField("__**" + HelpLocale.W_USAGE + ":**__",
                PrefixData.getPrefix(channel.getGuild(), null) + getCommandName() + " " + args,
                false, channel));

        StringBuilder desc = new StringBuilder();
        if (commandArgs.length != 0) {
            String title = "__**" + HelpLocale.W_ARGUMENTS + ":**__";
            for (CommandArg arg : commandArgs) {
                desc.setLength(0);
                desc.append(">>> ").append(TextLocalizer.fastLocale(arg.getArgHelpString(), channel.getGuild()))
                        .append(lineSeparator()).append(lineSeparator());
                builder.addField(new LocalizedField(title, desc.toString(),
                        false, channel));
                title = "";
            }
        }
        builder.setTitle("__**" + HelpLocale.M_HELP_FOR_COMMAND + " " + getCommandName() + "**__")
                .setColor(Color.green);

        channel.sendMessage(builder.build()).queue();
    }

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
}