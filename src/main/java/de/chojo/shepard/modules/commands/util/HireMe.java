package de.chojo.shepard.modules.commands.util;

import de.chojo.shepard.Messages;
import de.chojo.shepard.modules.commands.Command;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;

public class HireMe extends Command {

    public HireMe() {
        commandName = "hireMe";
        commandAliases = new String[]{"Iwantyou", "lovemeshepard"};
        commandDesc = "Get a link to invite me!";
        args = null;
    }

    @Override
    public boolean execute(String[] args, MessageChannel channel, MessageReceivedEvent receivedEvent) {
        ArrayList<MessageEmbed.Field> fields = new ArrayList();
        if (args[0].equalsIgnoreCase(commandName)) {
            fields.add(new MessageEmbed.Field("You wanna hire me? Please give me 100k Credits or click on the link",
                    "http://bit.ly/shepardbot", false));
        } else if (args[0].equalsIgnoreCase(commandAliases[0])) {
            fields.add(new MessageEmbed.Field("I want you too! And I wanna be where you are!",
                    "http://bit.ly/shepardbot", false));
        } else if (args[0].equalsIgnoreCase(commandAliases[1])) {
            fields.add(new MessageEmbed.Field("I love you! Please take me!",
                    "http://bit.ly/shepardbot", false));
        }
        Messages.sendTextBox(null, fields, channel);

        return true;

    }
}
