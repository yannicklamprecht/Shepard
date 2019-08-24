package de.chojo.shepard.listener;

import de.chojo.shepard.collections.CommandCollection;
import de.chojo.shepard.database.queries.Prefix;
import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.contexts.commands.Command;
import de.chojo.shepard.contexts.commands.exceptions.CommandException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;


public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String receivedMessage = message.getContentRaw();
        String[] args = receivedMessage.split(" ");
        args[0] = args[0].replace(Prefix.getPrefix(event.getGuild(), event), "");

        if (checkPrefix(receivedMessage, event.getGuild(), event)) {
            //BotCheck
            if (event.getAuthor().isBot()) {
                Messages.sendMessage("I'm not allowed to talk to you " + event.getAuthor().getName()
                        + ". Please leave me alone ._.", event.getChannel());
                return;
            }

            //Command execution
            Command command = CommandCollection.getInstance().getCommand(args[0]);
            if (command != null && command.isContextValid(event)) {
                //TODO Check Arg length of command
                String label = args[0];

                if (args.length > 1) {
                    args = Arrays.copyOfRange(args, 1, args.length);
                } else {
                    args = new String[0];
                }
                if (command.checkArguments(args)) {
                    try {
                        command.execute(label, args, event);
                    } catch (CommandException e) {
                        Messages.sendSimpleError(e.getMessage(), event.getChannel());
                    }
                } else {
                    Messages.sendSimpleError("Too few Arguments", event.getChannel());
                    command.sendCommandUsage(event.getChannel());
                }
                return;
            }

            Messages.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("Command not found!", "Type "
                    + Prefix.getPrefix(event.getGuild(), event)
                    + "help for a full list of available commands!", false)}, event.getChannel());
        }
    }

    private boolean checkPrefix(String message, Guild guild, MessageReceivedEvent event) {
        return message.startsWith(Prefix.getPrefix(event.getGuild(), event));
    }
}

