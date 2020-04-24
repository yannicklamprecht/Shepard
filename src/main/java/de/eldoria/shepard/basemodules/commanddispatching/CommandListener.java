package de.eldoria.shepard.basemodules.commanddispatching;

import de.eldoria.shepard.basemodules.commanddispatching.util.ExecutionValidator;
import de.eldoria.shepard.basemodules.reactionactions.ReactionActionCollection;
import de.eldoria.shepard.basemodules.reactionactions.actions.ExecuteCommand;
import de.eldoria.shepard.basemodules.reactionactions.actions.SendCommandHelp;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.CommandUtil;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.prefix.PrefixData;
import de.eldoria.shepard.commandmodules.repeatcommand.LatestCommandsCollection;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.modulebuilder.requirements.ReqCommands;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqCooldownManager;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqExecutionValidator;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqJDA;
import de.eldoria.shepard.modulebuilder.requirements.ReqLatestCommands;
import de.eldoria.shepard.modulebuilder.requirements.ReqReactionAction;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_COMMAND_NOT_FOUND;
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_HELP_COMMAND;
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_INSUFFICIENT_PERMISSION;
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_SUGGESTION;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

@Slf4j
public class CommandListener extends ListenerAdapter
        implements ReqJDA, ReqCommands, ReqLatestCommands, ReqCooldownManager,
        ReqExecutionValidator, ReqReactionAction, ReqDataSource, ReqConfig, ReqInit {
    private JDA jda;
    private CommandHub commands;
    private CooldownManager cooldownManager;
    private LatestCommandsCollection latest;
    private ExecutionValidator executionValidator;
    private ReactionActionCollection reactionAction;
    private PrefixData prefixData;
    private DataSource source;
    private Config config;

    /**
     * Create a new command listener.
     */
    public CommandListener() {
    }

    @Override
    public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
        if (event.getMessage().getTimeCreated().isAfter(OffsetDateTime.now().minusMinutes(5))) {
            onCommand(new MessageEventDataWrapper(event));
        }
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        onCommand(new MessageEventDataWrapper(event));
    }

    /**
     * Checks if the message is a Command on the guild.
     * Suggests a command if no command is valid and some similar command are found.
     * Parses the input into commands.
     *
     * @param messageContext context to check
     */
    private void onCommand(MessageEventDataWrapper messageContext) {
        String receivedMessage = messageContext.getMessage().getContentRaw();
        receivedMessage = receivedMessage.replaceAll("\\s\\s+", " ");
        String[] args = receivedMessage.split(" ");

        // Check if message is command
        if (!isCommand(receivedMessage, args, messageContext)) return;

        // Ignore if the command is send by shepard
        if (Verifier.equalSnowflake(messageContext.getAuthor(), jda.getSelfUser())
                || messageContext.getAuthor().isBot()) {
            return;
        }

        args = stripArgs(receivedMessage, args, messageContext);
        String label = args[0];
        args = buildArgs(args);

        // Find the executed command.
        Optional<Command> command = commands.getCommand(label);

        if (command.isPresent()) {
            dispatchCommand(command.get(), label, args, messageContext);
            return;
        }
        String prefix = prefixData.getPrefix(messageContext.getGuild(), messageContext);

        if (searchAndSendSuggestion(messageContext, args, label, prefix)) return;

        MessageSender.sendError(
                new LocalizedField[] {
                        new LocalizedField(M_COMMAND_NOT_FOUND.tag, localizeAllAndReplace(M_HELP_COMMAND.tag,
                                messageContext.getGuild(), "`" + prefix + "help`"), false, messageContext)},
                messageContext.getTextChannel());
    }

    private boolean searchAndSendSuggestion(MessageEventDataWrapper messageContext,
                                            String[] args, String label, String prefix) {
        List<Command> similarCommand = commands.getSimilarCommands(label);
        if (!similarCommand.isEmpty()) {
            for (Command cmd : similarCommand) {
                if (!executionValidator.canAccess(cmd, messageContext)) continue;
                LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(messageContext.getGuild())
                        .setTitle(M_COMMAND_NOT_FOUND.tag)
                        .setColor(Color.green)
                        .setDescription(M_SUGGESTION + System.lineSeparator() + "**" + cmd.getCommandName() + "**")
                        .setThumbnail(ShepardReactions.WINK.thumbnail);

                messageContext.getTextChannel().sendMessage(builder.build()).queue(m ->
                        reactionAction.addReactionAction(
                                m, new ExecuteCommand(commands, messageContext.getAuthor(), cmd, args, messageContext),
                                new SendCommandHelp(cmd, prefix)));
                return true;
            }
        }
        return false;
    }

    private void dispatchCommand(Command command, String label, String[] args, MessageEventDataWrapper messageContext) {
        //Check if the context can be used on guild by user
        if (!executionValidator.canAccess(command, messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_COMMAND_NOT_FOUND.tag, messageContext.getGuild()),
                    messageContext.getTextChannel());
            return;
        }

        //check if the user has the permission on the guild
        if (!executionValidator.canUse(command, messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_INSUFFICIENT_PERMISSION.tag,
                    messageContext.getGuild(), "**" + command.getCommandName() + "**"),
                    messageContext.getTextChannel());
            return;
        }

        //Check if it is the help command
        if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
            sendHelpText(command, messageContext.getTextChannel());
            return;
        }

        //Check if the argument count is equal or more than the minimum arguments
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

        commands.runCommand(command, label, args, messageContext);

        cooldownManager.renewCooldown(command, messageContext.getGuild(), messageContext.getAuthor());
        latest.saveLatestCommand(messageContext.getGuild(), messageContext.getAuthor(), command, label, args);
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

    @NotNull
    private String[] buildArgs(String[] args) {
        String[] newArgs;
        if (args.length > 1) {
            newArgs = Arrays.copyOfRange(args, 1, args.length);
        } else {
            newArgs = new String[0];
        }
        return newArgs;
    }

    private boolean isCommand(String receivedMessage, String[] args, MessageEventDataWrapper messageContext) {
        boolean isCommand = false;
        String prefix = prefixData.getPrefix(messageContext.getGuild(), messageContext);
        if (receivedMessage.startsWith(prefix)) {
            isCommand = true;
            //Check if the message is a command executed by a mention of the bot.
        } else if (DbUtil.getIdRaw(args[0]).contentEquals(jda.getSelfUser().getId())) {
            isCommand = true;
        }
        return isCommand;
    }

    private String[] stripArgs(String receivedMessage, String[] args, MessageEventDataWrapper messageContext) {
        String prefix = prefixData.getPrefix(messageContext.getGuild(), messageContext);
        String[] strippedArgs;
        if (receivedMessage.startsWith(prefix)) {
            args[0] = args[0].substring(prefix.length());
            strippedArgs = args;
            //Check if the message is a command executed by a mention of the bot.
        } else if (DbUtil.getIdRaw(args[0]).contentEquals(jda.getSelfUser().getId())) {
            strippedArgs = Arrays.copyOfRange(args, 1, args.length);
        } else {
            strippedArgs = args;
        }
        return strippedArgs;
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


    @Override
    public void addCommands(CommandHub commandHub) {
        this.commands = commandHub;
    }

    @Override
    public void addCooldownManager(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    @Override
    public void addJDA(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void addLatestCommand(LatestCommandsCollection latestCommands) {
        this.latest = latestCommands;
    }

    @Override
    public void addExecutionValidator(ExecutionValidator validator) {
        this.executionValidator = validator;
    }

    @Override
    public void addReactionAction(ReactionActionCollection reactionAction) {
        this.reactionAction = reactionAction;
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    @Override
    public void addConfig(Config config) {
        this.config = config;
    }

    @Override
    public void init() {
        prefixData = new PrefixData(source, config);
    }
}

