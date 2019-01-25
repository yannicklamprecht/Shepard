package de.chojo.shepard.listener;

import de.chojo.shepard.Collections.CommandCollection;
import de.chojo.shepard.Collections.ServerCollection;
import de.chojo.shepard.Messages;
import de.chojo.shepard.modules.commands.Command;
import de.chojo.shepard.Settings;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class CommandListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String receivedMessage = message.getContentRaw();
        String args[] = receivedMessage.replace(Settings.getPrefix(), "").split(" ");

        if (checkPrefix(receivedMessage)) {
            //BotCheck
            if (event.getAuthor().isBot()) {
                Messages.sendMessage("I'm not allowed to talk to you " + event.getAuthor().getName() + ". Please leave me alone ._.", event.getChannel());
                return;
            }

            //Command execution
            for (Command command : CommandCollection.getInstance().getCommands()) {
                if (command.isCommand(args[0])) {
                    if (command.isCommandValid(event)) {
                        command.execute(args, event.getChannel(), event);
                        Messages.LogMessageAsEmbedded(event, ServerCollection.getNormandy().getTextChannelById("538087478960324630"));
                        return;
                    }
                }
            }

            Messages.sendError(new MessageEmbed.Field[]{new MessageEmbed.Field("Command not found!", "Type " + Settings.getPrefix() + "help for a full list of available commands!", false)}, event.getChannel());
        }
    }

    private boolean checkPrefix(String message) {
        return message.startsWith(Settings.getPrefix());

    }


}

