package de.eldoria.shepard.listener;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.queries.PrefixData;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.exceptions.CommandException;
import de.eldoria.shepard.messagehandler.ShepardReactions;
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
            onCommand(new MessageEventDataWrapper<>(event));
        }
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        onCommand(new MessageEventDataWrapper<>(event));
    }

    private void onCommand(MessageEventDataWrapper wrapper) {
        if (wrapper.getChannel() instanceof PrivateChannel) {
            if (wrapper.getAuthor().isBot()) return;
            wrapper.getChannel().sendMessage("I'm too shy. Please speak to me on a public Server.").queue();
            return;
        }


        String receivedMessage = wrapper.getMessage().getContentRaw();
        String[] args = receivedMessage.split(" ");

        boolean isCommand = false;

        if (checkPrefix(receivedMessage, wrapper)) {
            isCommand = true;
            args[0] = args[0].replaceFirst(PrefixData.getPrefix(wrapper.getGuild(), wrapper), "");

        } else if (DbUtil.getIdRaw(args[0]).contentEquals(ShepardBot.getJDA().getSelfUser().getId())) {
            args = Arrays.copyOfRange(args, 1, args.length);
            isCommand = true;
        }

        String label = args[0];

        if (isCommand) {
            //BotCheck
            if (wrapper.getAuthor().isBot()) {
                MessageSender.sendMessage("I'm not allowed to talk to you " + wrapper.getAuthor().getName()
                        + ". Please leave me alone ._.", wrapper.getChannel());
                return;
            }

            //Command execution
            Command command = CommandCollection.getInstance().getCommand(args[0]);
            if (command != null && command.isContextValid(wrapper)) {
                //TODO Check Arg length of command

                if (args.length > 1) {
                    args = Arrays.copyOfRange(args, 1, args.length);
                } else {
                    args = new String[0];
                }
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

            List<Command> similarCommand = CommandCollection.getInstance().getSimilarCommands(args[0]);
            if (similarCommand.size() != 0) {
                for (Command cmd : similarCommand) {
                    if (cmd.isContextValid(wrapper)) {
                        MessageSender.sendSimpleTextBox("Command not found!", "I don't have a command with this name. "
                                + "Maybe you meant: " + System.lineSeparator() + "**" + cmd.getCommandName() + "**", Color.green, ShepardReactions.WINK, wrapper.getChannel());
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

