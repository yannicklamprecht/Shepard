package de.eldoria.shepard.listener;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.queries.PrefixData;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.InteractableMessageSender;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.exceptions.CommandException;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.reactionactions.ExecuteCommand;
import de.eldoria.shepard.reactionactions.SendCommandHelp;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;


public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
        if (event.getMessage().getTimeCreated().isAfter(OffsetDateTime.now().minusMinutes(5))) {
            onCommand(new MessageEventDataWrapper(event));
        }
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        onCommand(new MessageEventDataWrapper(event));
    }

    private void onCommand(MessageEventDataWrapper messageContext) {
        if (messageContext.getChannel() instanceof PrivateChannel) {
            if (messageContext.getAuthor().isBot()) return;
            messageContext.getChannel().sendMessage("I'm too shy. Please speak to me on a public Server.").queue();
            return;
        }


        String receivedMessage = messageContext.getMessage().getContentRaw();
        String[] args = receivedMessage.split(" ");

        boolean isCommand = false;

        if (checkPrefix(receivedMessage, messageContext)) {
            isCommand = true;
            args[0] = args[0].replaceFirst(PrefixData.getPrefix(messageContext.getGuild(), messageContext), "");

        } else if (DbUtil.getIdRaw(args[0]).contentEquals(ShepardBot.getJDA().getSelfUser().getId())) {
            args = Arrays.copyOfRange(args, 1, args.length);
            isCommand = true;
        }

        String label = args[0];

        if (isCommand) {
            //BotCheck
            if (messageContext.getAuthor().isBot()) {
                MessageSender.sendMessage("I'm not allowed to talk to you " + messageContext.getAuthor().getName()
                        + ". Please leave me alone ._.", messageContext.getChannel());
                return;
            }

            //Command execution
            Command command = CommandCollection.getInstance().getCommand(label);

            if (args.length > 1) {
                args = Arrays.copyOfRange(args, 1, args.length);
            } else {
                args = new String[0];
            }
            if (command != null && command.isContextValid(wrapper)) {
                if (command.checkArguments(args)) {
                    try {
                        command.execute(label, args, wrapper);
                    } catch (CommandException | InsufficientPermissionException e) {
                        MessageSender.sendSimpleError(e.getMessage(), wrapper.getChannel());
                    }
                } else {
                    MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, wrapper.getChannel());
                    command.sendCommandUsage(wrapper.getChannel());
                }
                return;
            }

            List<Command> similarCommand = CommandCollection.getInstance().getSimilarCommands(label);
            if (similarCommand.size() != 0) {
                for (Command cmd : similarCommand) {
                    if (cmd.isContextValid(wrapper)) {
                        InteractableMessageSender.sendSimpleTextBox("Command not found!",
                                "I don't have a command with this name. Maybe you meant: "
                                        + System.lineSeparator() + "**" + cmd.getCommandName() + "**",
                                Color.green, ShepardReactions.WINK, wrapper.getTextChannel(),
                                new ExecuteCommand(wrapper.getAuthor(),cmd,args,wrapper),
                                new SendCommandHelp(cmd, wrapper));
                        return;
                    }
                }
            }

            MessageSender.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("Command not found!", "Type "
                    + PrefixData.getPrefix(wrapper.getGuild(), wrapper)
                    + "help for a full list of available commands!", false)}, wrapper.getChannel());

        }

    }

    private boolean checkPrefix(String message, MessageEventDataWrapper event) {
        return message.startsWith(PrefixData.getPrefix(event.getGuild(), event));

    }
}

