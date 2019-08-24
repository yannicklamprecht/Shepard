package de.chojo.shepard.contexts.commands.util;

import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.contexts.commands.Command;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * A command for retrieving a invite link for this bot.
 */
public class HireMe extends Command {

    /**
     * Creates new Hire me object.
     */
    public HireMe() {
        commandName = "hireMe";
        commandAliases = new String[]{"Iwantyou", "lovemeshepard"};
        commandDesc = "Get a link to invite me!";
        arguments = null;
    }

    @Override
    public boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        List<MessageEmbed.Field> fields = new ArrayList<>();
        if (label.equalsIgnoreCase(commandName)) {
            fields.add(new MessageEmbed.Field("You wanna hire me? Please give me 100k Credits or click on the link",
                    "http://bit.ly/shepardbot", false));
        } else if (label.equalsIgnoreCase(commandAliases[0])) {
            fields.add(new MessageEmbed.Field("I want you too! And I wanna be where you are!",
                    "http://bit.ly/shepardbot", false));
        } else if (label.equalsIgnoreCase(commandAliases[1])) {
            fields.add(new MessageEmbed.Field("I love you! Please take me!",
                    "http://bit.ly/shepardbot", false));
        }
        Messages.sendTextBox(null, fields, receivedEvent.getChannel());

        return true;

    }
}
