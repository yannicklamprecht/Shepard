package de.eldoria.shepard.listener;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.queries.PrefixData;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.exceptions.CommandException;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.SQLException;
import java.util.Arrays;


public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannel() instanceof PrivateChannel) {
            if (event.getAuthor().isBot()) return;
            event.getChannel().sendMessage("I'm too shy. Please speak to me on a public Server.").queue();
            return;
        }


        String receivedMessage = event.getMessage().getContentRaw();
        String[] args = receivedMessage.split(" ");

        boolean isCommand = false;

        if (checkPrefix(receivedMessage, event)) {
            isCommand = true;
            try {
                args[0] = args[0].replaceFirst(PrefixData.getPrefix(event.getGuild(), event), "");
            } catch (SQLException e) {
                return;
            }

        } else if (DbUtil.getIdRaw(args[0]).contentEquals(ShepardBot.getJDA().getSelfUser().getId())) {
            args = Arrays.copyOfRange(args, 1, args.length);
            isCommand = true;
        }

        String label = args[0];

        if (isCommand) {
            //BotCheck
            if (event.getAuthor().isBot()) {
                MessageSender.sendMessage("I'm not allowed to talk to you " + event.getAuthor().getName()
                        + ". Please leave me alone ._.", event.getChannel());
                return;
            }

            //Command execution
            Command command = CommandCollection.getInstance().getCommand(args[0]);
            if (command != null && command.isContextValid(event)) {
                //TODO Check Arg length of command

                if (args.length > 1) {
                    args = Arrays.copyOfRange(args, 1, args.length);
                } else {
                    args = new String[0];
                }
                if (command.checkArguments(args)) {
                    try {
                        command.execute(label, args, event);
                    } catch (CommandException | InsufficientPermissionException e) {
                        MessageSender.sendSimpleError(e.getMessage(), event.getChannel());
                    }
                } else {
                    MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, event.getChannel());
                    command.sendCommandUsage(event.getChannel());
                }
                return;
            }
            try {
                MessageSender.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("Command not found!", "Type "
                        + PrefixData.getPrefix(event.getGuild(), event)
                        + "help for a full list of available commands!", false)}, event.getChannel());
            } catch (SQLException e) {
                return;
            }

        }
    }

    private boolean checkPrefix(String message, MessageReceivedEvent event) {
        try {
            return message.startsWith(PrefixData.getPrefix(event.getGuild(), event));
        } catch (SQLException e) {
            return false;
        }

    }
}

