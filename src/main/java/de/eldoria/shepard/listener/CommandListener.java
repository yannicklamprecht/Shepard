package de.eldoria.shepard.listener;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.queries.PrefixData;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.InteractableMessageSender;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.reactionactions.ExecuteCommand;
import de.eldoria.shepard.reactionactions.SendCommandHelp;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_BOT_ANSWER;
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_COMMAND_NOT_FOUND;
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_HELP_COMMAND;
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_INSUFFICIENT_PERMISSION;
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_SUGGESTION;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;


public class CommandListener extends ListenerAdapter {
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

    private void onCommand(MessageEventDataWrapper messageContext) {
        String receivedMessage = messageContext.getMessage().getContentRaw();
        receivedMessage = receivedMessage.replaceAll("\\s\\s+", " ");
        String[] args = receivedMessage.split(" ");

        boolean isCommand = false;

        if (Verifier.checkPrefix(receivedMessage, messageContext)) {
            isCommand = true;
            args[0] = args[0].replaceFirst(PrefixData.getPrefix(messageContext.getGuild(), messageContext), "");

            //Check if the message is a command executed by a mention of the bot.
        } else if (DbUtil.getIdRaw(args[0]).contentEquals(ShepardBot.getJDA().getSelfUser().getId())) {
            args = Arrays.copyOfRange(args, 1, args.length);
            isCommand = true;
        }


        if (!isCommand) return;

        // Ignore if the command is send by shepard
        if (messageContext.getAuthor().getIdLong() == ShepardBot.getJDA().getSelfUser().getIdLong()) {
            return;
        }

        //Return if command is send by another bot
        if (messageContext.getAuthor().isBot()) {
            MessageSender.sendMessage(localizeAllAndReplace(M_BOT_ANSWER.tag, messageContext.getGuild(),
                    "**" + messageContext.getAuthor().getName() + "**"), messageContext.getTextChannel());
            return;
        }

        String label = args[0];
        // Find the executed command.
        Command command = CommandCollection.getInstance().getCommand(label);

        if (args.length > 1) {
            args = Arrays.copyOfRange(args, 1, args.length);
        } else {
            args = new String[0];
        }

        if (command != null && command.isContextValid(messageContext)) {
            if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
                command.sendCommandUsage(messageContext.getTextChannel());
                return;
            }
            if (command.checkArguments(args)) {
                command.executeAsync(label, args, messageContext);
            } else {
                try {
                    MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
                } catch (InsufficientPermissionException ex) {
                    messageContext.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel ->
                            MessageSender.handlePermissionException(ex, messageContext.getTextChannel()));
                }
            }
            return;
        } else if (command != null && command.canBeExecutedHere(messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_INSUFFICIENT_PERMISSION.tag,
                    messageContext.getGuild(), command.getContextName()), messageContext.getTextChannel());
            return;
        }

        List<Command> similarCommand = CommandCollection.getInstance().getSimilarCommands(label);
        if (similarCommand.size() != 0) {
            for (Command cmd : similarCommand) {
                if (!cmd.isContextValid(messageContext)) continue;

                InteractableMessageSender.sendSimpleTextBox(M_COMMAND_NOT_FOUND.tag,
                        M_SUGGESTION + System.lineSeparator() + "**" + cmd.getCommandName() + "**",
                        Color.green, ShepardReactions.WINK, messageContext.getTextChannel(),
                        new ExecuteCommand(messageContext.getAuthor(), cmd, args, messageContext),
                        new SendCommandHelp(cmd));
                return;
            }
        }

        MessageSender.sendError(
                new LocalizedField[] {
                        new LocalizedField(M_COMMAND_NOT_FOUND.tag, localizeAllAndReplace(M_HELP_COMMAND.tag,
                                messageContext.getGuild(), "`" + PrefixData.getPrefix(messageContext.getGuild(),
                                        messageContext) + "help`"), false, messageContext)},
                messageContext.getTextChannel());

    }
}

