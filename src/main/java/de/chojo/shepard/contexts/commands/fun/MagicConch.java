package de.chojo.shepard.contexts.commands.fun;

import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.contexts.commands.Command;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Random;


public class MagicConch extends Command {

    public MagicConch() {
        commandName = "MagicConch";
        commandAliases = new String[] {"MagischeMiesmuschel"};
        commandDesc = "Find your decision!";
        arguments = null;
    }

    String[] positive = new String[] {"Yes", "Of Course", "DO IT!", "Why not :)"};
    String[] negative = new String[] {"Are you dumb?", "Fly, you fool!", "nope", "No"};
    String[] neutral = new String[] {"How should I know?", "Maybe", "You should ask someone else instead."};

    @Override
    public boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String word = "";
        Random rand = new Random();
        int type = rand.nextInt(3);

        if (type == 0) {
            word = positive[rand.nextInt(positive.length)];
        } else if (type == 1) {
            word = negative[rand.nextInt(negative.length)];
        } else if (type == 2) {
            word = neutral[rand.nextInt(neutral.length)];
        }


        ArrayList<MessageEmbed.Field> fields = new ArrayList<>();

        fields.add(new MessageEmbed.Field("The magic conch says:", word, false));

        Messages.sendTextBox(null, fields, receivedEvent.getChannel());

        return true;
    }
}
