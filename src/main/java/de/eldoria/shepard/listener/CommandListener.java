package de.eldoria.shepard.listener;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.queries.commands.PrefixData;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.messagehandler.InteractableMessageSender;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.reactionactions.ExecuteCommand;
import de.eldoria.shepard.reactionactions.SendCommandHelp;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_BOT_ANSWER;
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_COMMAND_NOT_FOUND;
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_HELP_COMMAND;
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

        if (command != null) {
            command.executeAsync(label, args, messageContext);
            return;
        }

        List<Command> similarCommand = CommandCollection.getInstance().getSimilarCommands(label);
        if (!similarCommand.isEmpty()) {
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

