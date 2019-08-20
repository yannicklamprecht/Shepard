package de.chojo.shepard.listener;

import de.chojo.shepard.ShepardBot;
import de.chojo.shepard.collections.CommandCollection;
import de.chojo.shepard.collections.ServerCollection;
import de.chojo.shepard.database.queries.Prefix;
import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.modules.commands.Command;
import de.chojo.shepard.Settings;
import de.chojo.shepard.modules.commands.admin.CommandException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Map;


public class CommandListener extends ListenerAdapter {
    public CommandListener(){
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String receivedMessage = message.getContentRaw();
        String[] args = receivedMessage.replace(
                Prefix.getPrefixes().getOrDefault(event.getGuild().getId())
                , "").split(" ");

        if (checkPrefix(receivedMessage, event.getGuild())) {
            //BotCheck
            if (event.getAuthor().isBot()) {
                Messages.sendMessage("I'm not allowed to talk to you " + event.getAuthor().getName()
                        + ". Please leave me alone ._.", event.getChannel());
                return;
            }

            //Command execution
            for (Command command : CommandCollection.getInstance().getCommands()) {
                if (command.isCommand(args[0])) {
                    if (command.isCommandValid(event)) {
                        //TODO Check Arg length of command
                        try {
                            command.execute(args, event);
                        } catch (CommandException e) {
                            Messages.sendSimpleError(e.getMessage(), event.getChannel());
                        }
                        Messages.logMessageAsEmbedded(event, ServerCollection.getNormandy().getTextChannelById("538087478960324630"));
                        return;
                    }
                }
            }

            Messages.sendError(new MessageEmbed.Field[]{new MessageEmbed.Field("Command not found!", "Type "
                    + Prefix.getPrefixes().getOrDefault(event.getGuild().getId())
                    + "help for a full list of available commands!", false)}, event.getChannel());
        }
    }

    private boolean checkPrefix(String message, Guild guild) {
        return message.startsWith(Prefix.getPrefixes().getOrDefault(guild.getId()));
    }
}

