package de.chojo.shepard.modules.commands.Fun;

import de.chojo.shepard.Messages;
import de.chojo.shepard.modules.commands.Command;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.ArrayList;
import java.util.Random;


public class MagicConch extends Command {

    public MagicConch() {
        commandName = "MagicConch";
        commandAliases = new String[]{"MagischeMiesmuschel"};
        commandDesc = "Find your decision!";
        args = null;
    }

    String[] positive = new String[]{"Yes", "Of Course", "DO IT!", "Why not :)"};
    String[] negative = new String[]{"Are you dumb?", "Fly, you fool!", "nope", "No"};
    String[] neutral = new String[]{"How should I know?", "Maybe", "You should ask someone else instead."};

    @Override
    public boolean execute(String[] args, MessageChannel channel, MessageReceivedEvent receivedEvent) {
        Integer type = 0;
        String word = "";
        Random rand = new Random();

        type = rand.nextInt(3);

        if (type == 0)
            word = positive[rand.nextInt(positive.length)];
        else if (type == 1)
            word = negative[rand.nextInt(negative.length)];
        else if (type == 2)
            word = neutral[rand.nextInt(neutral.length)];


        ArrayList<MessageEmbed.Field> fields = new ArrayList<>();

        fields.add(new MessageEmbed.Field("The magic conch says:", word, false));

        Messages.sendTextBox(null, fields, channel);

        return true;
    }
}
